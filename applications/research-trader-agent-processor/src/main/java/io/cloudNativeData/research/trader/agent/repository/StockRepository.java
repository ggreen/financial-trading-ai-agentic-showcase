package io.cloudNativeData.research.trader.agent.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface StockRepository {
    BigDecimal calculateMovingAverage200(String stockId);
}
