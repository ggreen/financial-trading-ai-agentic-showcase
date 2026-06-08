package io.cloudNativeData.portfolio.agent.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PortfolioRepository {

    BigDecimal findTotalPortfolioValue();

    BigDecimal findMaxAllocationPerTrade();
}
