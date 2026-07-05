package io.cloudNativeData.research.trader.agent.listener;

import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.util.CqListenerAdapter;
import org.springframework.data.gemfire.listener.ContinuousQueryListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class BuyStockListener extends CqListenerAdapter implements ContinuousQueryListener {
    private final ResearchTraderService researchTraderService;

    @Override
    public void onEvent(CqEvent cqEvent) {


        // The Region Key represents the stock ticker (e.g., "AAPL", "TSLA")

        if (cqEvent != null && cqEvent.getKey() != null) {
            var ticker = cqEvent.getKey().toString();

            log.info("BUY STOCK listener invoked.The stock event received: {}", ticker);

            // Invoke the specific service method
            researchTraderService.recommendBuy((StockPriceMovingAverage) cqEvent.getNewValue());
        }
    }
}

