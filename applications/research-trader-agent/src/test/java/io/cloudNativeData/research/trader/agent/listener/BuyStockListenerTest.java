package io.cloudNativeData.research.trader.agent.listener;

import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.query.CqEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyStockListenerTest {

    @Mock
    private ResearchTraderService researchTraderService;

    @Mock
    private CqEvent cqEvent;

    private BuyStockListener subject;
    private final StockPriceMovingAverage expectedMovingAverage = JavaBeanGeneratorCreator.of(StockPriceMovingAverage.class).create();

    @BeforeEach
    void setUp() {
        subject = new BuyStockListener(researchTraderService);
    }

    @Test
    void onEvent_ShouldInvokeRecommendBuy_WhenEventAndKeyAreValid() {
        // Arrange
        String ticker = "AAPL";

        // Stub the CqEvent behavior
        when(cqEvent.getKey()).thenReturn(ticker);
        when(cqEvent.getNewValue()).thenReturn(expectedMovingAverage);

        // Act
        subject.onEvent(cqEvent);

        // Assert
        verify(researchTraderService, times(1)).recommendBuy(expectedMovingAverage);
    }

    @Test
    void onEvent_ShouldNotInvokeRecommendBuy_WhenCqEventIsNull() {
        // Act
        subject.onEvent(null);

        // Assert
        verifyNoInteractions(researchTraderService);
    }

    @Test
    void onEvent_ShouldNotInvokeRecommendBuy_WhenKeyIsNull() {
        // Arrange
        when(cqEvent.getKey()).thenReturn(null);

        // Act
        subject.onEvent(cqEvent);

        // Assert
        verifyNoInteractions(researchTraderService);
        verify(cqEvent, never()).getNewValue();
    }
}