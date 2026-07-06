package io.cloudNativeData.research.trader.agent.repository;

import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

import java.util.List;

public interface StockPriceMovingAverageRepository extends GemfireRepository<StockPriceMovingAverage,String> {
    @Query("SELECT s.id FROM /StockPriceMovingAverage s")
    List<String> findAllIds();
}
