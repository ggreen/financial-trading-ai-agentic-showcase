package io.cloudNativeData.research.trader;

import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.*;
import org.apache.geode.pdx.PdxInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateMovingAverage200FunctionTest {

    private CalculateMovingAverage200Function subject;

    @Mock
    private FunctionContext<Object[]> fc;

    @Mock
    private RegionFunctionContext<Object[]> rfc;
    @Mock
    private ResultSender<Object> rs;
    @Mock
    private Region<String, PdxInstance> region;

    @Mock
    private PdxInstance stockDailyPrice;

    @Mock
    private Converter<Region,Region> toLocalRegion;

    private Collection<PdxInstance> regionValues;
    private String id=  "APPL";

    @BeforeEach
    void setUp() {
        subject =new CalculateMovingAverage200Function(toLocalRegion);
    }

    @Test
    void execute_throwsFunctionException_when_notOnRegion() {

        assertThrows(FunctionException.class, () -> subject.execute(fc));
    }

    @Test
    void execute_throws_arguments_are_null() {

        assertThrows(FunctionException.class, () -> subject.execute(rfc));
    }

    @Test
    void execute() {

        regionValues = new ArrayList<>();

        String[] args = { id};
        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getDataSet()).thenReturn((Region)region);
        when(rfc.getResultSender()).thenReturn(rs);
        when(toLocalRegion.convert(any())).thenReturn(region);

        when(region.values()).thenReturn(regionValues);

        subject.execute(rfc);


        verify(rs).lastResult(any());
    }

    @Test
    void testExecute_LimitsToMovingAveragePeriod() {
        // Arrange
        when(rfc.getDataSet()).thenReturn((Region)region);
        when(toLocalRegion.convert(any())).thenReturn(region);
        when(rfc.getArguments()).thenReturn(new Object[]{"AAPL"});
        when(rfc.getResultSender()).thenReturn(rs);

        // Generate 205 records (exceeding the MOVING_AVERAGE_PERIOD limit of 200)
        // Ensure the sorted system correctly isolates the latest 200 items.
        PdxInstance[] pricesArray = new PdxInstance[205];

        // Items 0 to 4 (oldest) will have a price of 0
        for (int i = 0; i < 5; i++) {
            pricesArray[i] = createStockPrice("AAPL", LocalDate.of(2020, 1, i + 1), BigDecimal.ZERO);
        }

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        long startEpochDay = startDate.toEpochDay();

        LocalDate priceDate = startDate;
        // Items 5 to 204 (newest 200 items) will have a price of 10
        for (int i = 5; i < 205; i++) {
            priceDate = LocalDate.ofEpochDay(priceDate.toEpochDay()+1);
            pricesArray[i] = createStockPrice("AAPL", priceDate, BigDecimal.TEN);
        }

        Collection<PdxInstance> regionValues = Arrays.asList(pricesArray);
        when(region.values()).thenReturn(regionValues);

        // Act
        subject.execute(rfc);

        // Assert
        // If limit works, it takes only the newest 200 items (all priced 10)
        // Average should be exactly 10.0000
        BigDecimal expectedAverage = BigDecimal.valueOf(10).setScale(4, RoundingMode.HALF_UP);
        verify(rs).lastResult(expectedAverage);
    }

    private PdxInstance createStockPrice(String ticker, LocalDate date, BigDecimal closePrice) {
        return new MockPdxInstance("className",
                Map.of("ticker",ticker, "id","ticker","priceDate",date,"closePrice",closePrice ), Set.of("id"),true);



    }
}