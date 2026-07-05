package io.cloudNativeData.research.trader.agent.functions.sink;

import io.cloudNativeData.research.trader.agent.repository.StockPriceMovingAverageRepository;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@AllArgsConstructor
public class SaveStockPriceMovingAverageSink implements Consumer<StockPriceMovingAverage> {

    private final StockPriceMovingAverageRepository repository;

    @Override
    public void accept(StockPriceMovingAverage stockPriceMovingAverage) {

        if (stockPriceMovingAverage != null) {
            this.repository.save(stockPriceMovingAverage);
        }
    }
}
