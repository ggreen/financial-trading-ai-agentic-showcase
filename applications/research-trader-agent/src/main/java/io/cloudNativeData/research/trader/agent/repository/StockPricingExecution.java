package io.cloudNativeData.research.trader.agent.repository;

import org.springframework.data.gemfire.function.annotation.OnRegion;

import java.math.BigDecimal;
import java.util.List;

@OnRegion(region="StockDailyPrice", resultCollector = "calculateMovingAverage200ResultsCollector")
public interface StockPricingExecution {
    BigDecimal calculateMovingAverage200(String stockId);
}
