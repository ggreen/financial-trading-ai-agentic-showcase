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
public class SellStockListener extends CqListenerAdapter implements ContinuousQueryListener {
    private final ResearchTraderService researchTraderService;

    @Override
    public void onEvent(CqEvent cqEvent) {
        // The Region Key represents the stock ticker (e.g., "AAPL", "TSLA")

        if(cqEvent == null)
        {
            log.info("cqEvent is null");
            return;
        }
        var op = cqEvent.getBaseOperation();

        // The Region Key represents the stock ticker (e.g., "AAPL", "TSLA")
        if(op ==null || op.isDestroy() || op.isClear() || op.isInvalidate() ||
                op.isRemoveAll())
        {
            log.info("Ignoring event: {}",cqEvent);
            return;
        }

        if (cqEvent.getKey() != null) {
            var ticker = cqEvent.getKey().toString();

            log.info("SELL STOCK listener invoked. Sell stock event received: {}", ticker);

            // Invoke the specific service method
            researchTraderService.recommendSell((StockPriceMovingAverage) cqEvent.getNewValue());
        }
    }
}

