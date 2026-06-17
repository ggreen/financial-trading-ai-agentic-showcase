package io.cloudNativeData.research.trader.agent.repository;

import io.cloudNativeData.trading.StockDailyPrice;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDailyPriceRepository extends GemfireRepository<StockDailyPrice,String> {
}
