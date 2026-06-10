package io.cloudNativeData.portfolio.agent.repository;

import java.math.BigDecimal;

public interface PortfolioRepository {

    BigDecimal findTotalPortfolioValue();

    BigDecimal findMaxAllocationPerTrade();

    int findMaxSellLimit(String ticker);

    int findBaseSellQuantity(String ticker);
}
