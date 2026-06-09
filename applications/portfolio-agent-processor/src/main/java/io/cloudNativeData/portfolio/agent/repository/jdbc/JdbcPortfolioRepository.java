package io.cloudNativeData.portfolio.agent.repository.jdbc;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class JdbcPortfolioRepository implements PortfolioRepository {

    private final JdbcTemplate jdbcTemplate;
    /*
    SELECT COALESCE(SUM(quantity * current_price), 0) FROM portfolio.portfolio_positions
     */
    private final static String findTotalPortfolioValueSql = """
            SELECT COALESCE(SUM(quantity * current_price), 0) FROM portfolio.portfolio_positions
            """;
    private final static String findMaxAllocationPerTradeSql = """
            SELECT COALESCE(
                (SELECT SUM(quantity * current_price) FROM portfolio.portfolio_positions) * (SELECT max_allocation_pct FROM portfolio.risk_profile LIMIT 1), 
                0
            )
            """;

    @Override
    public BigDecimal findTotalPortfolioValue() {

        var result = jdbcTemplate.queryForObject(findTotalPortfolioValueSql, BigDecimal.class);

        // Ensure we handle potential nulls safely, though COALESCE protects against it
        return Objects.requireNonNullElse(result, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal findMaxAllocationPerTrade() {

        var result = jdbcTemplate.queryForObject(findMaxAllocationPerTradeSql, BigDecimal.class);

        // Ensure we handle potential nulls safely, though COALESCE protects against it
        return Objects.requireNonNullElse(result, BigDecimal.ZERO);
    }
}
