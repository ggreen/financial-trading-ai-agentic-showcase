package io.cloudNativeData.portfolio.agent.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PortfolioRepository {

    @Query(value = "SELECT COALESCE(SUM(quantity * current_price), 0) FROM portfolio.portfolio_positions",
            nativeQuery = true)
    BigDecimal findTotalPortfolioValue();

    @Query(value = """
        SELECT COALESCE(
            (SELECT SUM(quantity * current_price) FROM portfolio.portfolio_positions) * (SELECT max_allocation_pct FROM risk_profile LIMIT 1), 
            0
        )
        """,
            nativeQuery = true)
    BigDecimal findMaxAllocationPerTrade();
}
