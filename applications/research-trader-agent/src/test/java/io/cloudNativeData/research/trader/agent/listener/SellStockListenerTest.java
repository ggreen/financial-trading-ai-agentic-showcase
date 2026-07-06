package io.cloudNativeData.research.trader.agent.listener;

import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SellStockListenerTest {

    @Mock
    private ResearchTraderService researchTraderService;

    @Mock
    private CqEvent cqEvent;


    @Mock
    private  StockPriceMovingAverage mockStockPriceMovingAverage ;

    @Mock
    private Operation operation;

    private SellStockListener sellStockListener;

    @BeforeEach
    void setUp() {
        // Inject the mocked service into the listener
        sellStockListener = new SellStockListener(researchTraderService);
    }

    @Test
    void testOnEvent_ShouldCallRecommendSellWithCorrectTicker() {
        // Given: A Geode event containing the stock ticker "NVDA" as the key
        String expectedTicker = "NVDA";
        when(cqEvent.getBaseOperation()).thenReturn(operation);
        when(cqEvent.getKey()).thenReturn(expectedTicker);

        when(cqEvent.getNewValue()).thenReturn(mockStockPriceMovingAverage);

        // When: The event fires
        sellStockListener.onEvent(cqEvent);

        // Then: Verify recommendSell was called exactly once with "NVDA"
        verify(researchTraderService, times(1)).recommendSell(mockStockPriceMovingAverage);
    }

    @Test
    void testOnEvent_WithNullKey_ShouldNotCallService() {
        when(cqEvent.getBaseOperation()).thenReturn(operation);
        // Given: An edge-case event where the key is missing or null
        when(cqEvent.getKey()).thenReturn(null);

        // When: The event fires
        sellStockListener.onEvent(cqEvent);

        // Then: The service should never be invoked
        verify(researchTraderService, never()).recommendSell(mockStockPriceMovingAverage);
    }
}