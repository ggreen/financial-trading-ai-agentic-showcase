package io.cloudNativeData.research.trader.agent.functions.gemfire;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.distributed.DistributedMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CalculateMovingAverage200ResultsCollectorTest {

    private CalculateMovingAverage200ResultsCollector subject;
    @Mock
    private DistributedMember mockMember;

    @Mock
    private DistributedMember mockMember2;


    @BeforeEach
    void setUp() {
        subject = new CalculateMovingAverage200ResultsCollector();
    }

    @Test
    void testGetResult_WhenNoResultsAdded_ReturnsBigDecimalZero() {
        BigDecimal result = subject.getResult();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void given_multi_results_when_one_server_is_zero_return_correct_average() {
        var expected = BigDecimal.TEN;
        subject.addResult(mockMember,BigDecimal.ZERO);
        subject.addResult(mockMember,expected);

        BigDecimal actual = subject.getResult();

        assertThat(actual.doubleValue()).isEqualTo(expected.doubleValue());
    }

    @Test
    void testAddResult_WithValidBigDecimals_CalculatesAverageCorrectly() {
        // Add sample averages from 3 different simulated nodes
        subject.addResult(mockMember, new BigDecimal("100.0000"));
        subject.addResult(mockMember, new BigDecimal("150.0000"));
        subject.addResult(mockMember, new BigDecimal("200.0000"));

        BigDecimal expectedAverage = new BigDecimal("150.0000")
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal actualAverage = subject.getResult();

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void testAddResult_WithDecimalRounding_RoundsToFourDecimalPlaces() {
        // Adding nodes that will force a recurring decimal fraction (e.g., 200 / 3 = 66.66666...)
        subject.addResult(mockMember, new BigDecimal("50.0000"));
        subject.addResult(mockMember, new BigDecimal("50.0000"));
        subject.addResult(mockMember, new BigDecimal("100.0000"));

        // (50 + 50 + 100) / 3 = 66.6667
        BigDecimal expectedAverage = new BigDecimal("66.6667");
        BigDecimal actualAverage = subject.getResult();

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void testAddResult_WithThrowable_ThrowsFunctionException() {
        RuntimeException nodeException = new RuntimeException("Node database connection timeout");

        // Verify that passing an exception wraps it directly into a Geode FunctionException
        FunctionException thrown = assertThrows(FunctionException.class, () -> {
            subject.addResult(mockMember, nodeException);
        });

        assertEquals(nodeException, thrown.getCause());
    }

    @Test
    void testAddResult_WithInvalidType_IsIgnored() {
        // Invalid data types (like a raw String) should be ignored by the implementation type guard
        subject.addResult(mockMember, "Invalid Data Payload");
        subject.addResult(mockMember, new BigDecimal("10.0000"));

        // Only the BigDecimal 10.0000 should count, making the average 10.0000 / 1 = 10.0000
        BigDecimal expected = new BigDecimal("10.0000").setScale(4, RoundingMode.HALF_UP);
        assertEquals(expected, subject.getResult());
    }

    @Test
    void testGetResultWithTimeout_InvokesBaseGetResult() throws Exception {
        subject.addResult(mockMember, new BigDecimal("42.0000"));

        BigDecimal result = subject.getResult(5, TimeUnit.SECONDS);

        assertEquals(new BigDecimal("42.0000").setScale(4, RoundingMode.HALF_UP), result);
    }

    @Test
    void testClearResults_EmptiesCollectedData() {
        subject.addResult(mockMember, new BigDecimal("50.0000"));

        subject.clearResults();

        // After clearing, it should act as if no data was ever collected
        assertEquals(BigDecimal.ZERO, subject.getResult());
    }
}