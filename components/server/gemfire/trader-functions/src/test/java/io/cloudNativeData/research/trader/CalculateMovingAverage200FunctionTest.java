package io.cloudNativeData.research.trader;

import io.cloudNativeData.trading.StockDailyPrice;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateMovingAverage200FunctionTest {

    private CalculateMovingAverage200Function subject;

    @Mock
    private FunctionContext<String[]> fc;

    @Mock
    private RegionFunctionContext<String[]> rfc;
    @Mock
    private ResultSender<Object> rs;
    @Mock
    private Region<String, StockDailyPrice> region;

    private final StockDailyPrice stockDailyPrice = JavaBeanGeneratorCreator.of(StockDailyPrice.class).create();

    @Mock
    private Converter<Region,Region> toLocalRegion;

    private Collection<StockDailyPrice> regionValues;

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

        String[] args = { stockDailyPrice.getId()};
        when(rfc.getArguments()).thenReturn(args);
        when(rfc.getDataSet()).thenReturn((Region)region);
        when(rfc.getResultSender()).thenReturn(rs);
        when(toLocalRegion.convert(any())).thenReturn(region);

        when(region.values()).thenReturn(regionValues);

        subject.execute(rfc);


        verify(rs).lastResult(any());
    }
}