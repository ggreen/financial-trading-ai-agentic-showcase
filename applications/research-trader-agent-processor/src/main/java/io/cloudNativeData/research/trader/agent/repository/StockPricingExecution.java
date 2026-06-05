package io.cloudNativeData.research.trader.agent.repository;

import org.springframework.data.gemfire.function.annotation.OnRegion;

import java.math.BigDecimal;

@OnRegion(region="StockDailyPrice")
public interface StockPricingExecution {
    BigDecimal calculateMovingAverage200(String stockId);
}
