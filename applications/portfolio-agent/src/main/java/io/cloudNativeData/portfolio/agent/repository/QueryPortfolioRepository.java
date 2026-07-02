package io.cloudNativeData.portfolio.agent.repository;

import java.math.BigDecimal;

public interface QueryPortfolioRepository {

    BigDecimal findTotalPortfolioValue();

    BigDecimal findMaxAllocationPerTrade();

    int findMaxSellLimit(String ticker);

    int findBaseSellQuantity(String ticker);
}
