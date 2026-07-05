package io.cloudNativeData.research.trader.agent.repository;

import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import org.springframework.data.gemfire.repository.GemfireRepository;

public interface StockPriceMovingAverageRepository extends GemfireRepository<StockPriceMovingAverage,String> {
}
