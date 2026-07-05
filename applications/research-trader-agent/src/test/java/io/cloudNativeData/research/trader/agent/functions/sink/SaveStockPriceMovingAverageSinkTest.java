package io.cloudNativeData.research.trader.agent.functions.sink;

import io.cloudNativeData.research.trader.agent.repository.StockPriceMovingAverageRepository;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveStockPriceMovingAverageSinkTest {

    @Mock
    private StockPriceMovingAverageRepository repository; // The dependency your sink interacts with

    private SaveStockPriceMovingAverageSink sink;
    private final StockPriceMovingAverage mockData = JavaBeanGeneratorCreator.of(StockPriceMovingAverage.class).create();

    @BeforeEach
    void setUp() {
        // Initialize the class under test with the mocked repository
        sink = new SaveStockPriceMovingAverageSink(repository);
    }

    @Test
    void accept_ShouldInvokeRepositorySave_WhenValidDataIsProvided() {
        // Arrange: Create a mock or dummy instance of your data object

        // Act: Call the method under test
        sink.accept(mockData);

        // Assert: Verify that the repository's save method was called exactly once with the expected data
        verify(repository, times(1)).save(any(StockPriceMovingAverage.class));
    }

    @Test
    void accept_ShouldNotInvokeRepositorySave_WhenDataIsNull() {
        // Act: Call the method with null to test defensive edge-cases
        sink.accept(null);

        // Assert: Ensure the repository was never touched
        verifyNoInteractions(repository);
    }
}