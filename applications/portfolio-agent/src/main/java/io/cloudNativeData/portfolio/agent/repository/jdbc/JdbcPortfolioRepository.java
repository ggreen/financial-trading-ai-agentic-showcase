package io.cloudNativeData.portfolio.agent.repository.jdbc;

import io.cloudNativeData.portfolio.agent.repository.QueryPortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class JdbcPortfolioRepository implements QueryPortfolioRepository {

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

    @Override
    public int findMaxSellLimit(String ticker) {
        // 1. Calculate Total Portfolio Value (Cash holdings + Stock holdings)
        var totalPortfolioSql = "SELECT SUM(quantity * current_price) FROM portfolio.portfolio_positions";
        var totalPortfolioValue = jdbcTemplate.queryForObject(totalPortfolioSql, BigDecimal.class);

        if (totalPortfolioValue == null || totalPortfolioValue.compareTo(BigDecimal.ZERO) <= 0) {
            return 0; // Guard rail: Empty portfolio or error
        }

        // 2. Fetch the current price and owned quantity of the target stock
        String stockSql = "SELECT current_price, quantity FROM portfolio.portfolio_positions WHERE ticker = ?";

        return jdbcTemplate.query(stockSql, rs -> {
            if (!rs.next()) {
                return 0; // Guard rail: We don't own this stock
            }

            var currentPrice = rs.getBigDecimal("current_price");
            var ownedQuantity = rs.getBigDecimal("quantity");

            if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
                return 0;
            }

            // 3. Fetch the allocation risk profile multiplier (Config ID: 1)
            var riskSql = "SELECT max_allocation_pct FROM portfolio.risk_profile WHERE id = 1";
            var maxAllocationPct = jdbcTemplate.queryForObject(riskSql, BigDecimal.class);

            if (maxAllocationPct == null) {
                maxAllocationPct = new BigDecimal("0.0500"); // Fallback safety default (5%)
            }

            // 4. Calculate the maximum capital value allowed to be liquidated in one block
            var maxAllowedDollarValue = totalPortfolioValue.multiply(maxAllocationPct);

            // 5. Convert dollar allowance to share quantity
            var maxSharesByRiskRule = maxAllowedDollarValue.divide(currentPrice, RoundingMode.DOWN).intValue();

            // 6. Risk Cap Ceiling: You can never sell more shares than you actually own
            return Math.min(maxSharesByRiskRule, ownedQuantity.intValue());
        }, ticker);
    }

    @Override
    public int findBaseSellQuantity(String ticker) {
        // 1. Fetch available cash (USD) from the portfolio
        String cashSql = "SELECT quantity FROM portfolio.portfolio_positions WHERE asset_type = 'CASH' AND ticker = 'USD'";
        BigDecimal availableCash = jdbcTemplate.queryForObject(cashSql, BigDecimal.class);

        if (availableCash == null || availableCash.compareTo(BigDecimal.ZERO) <= 0) {
            return 0; // Guard rail: No liquid cash available to baseline against
        }

        // 2. Fetch the current market price of the target stock
        String priceSql = "SELECT current_price FROM portfolio.portfolio_positions WHERE asset_type = 'STOCK' AND ticker = ?";

        BigDecimal currentPrice;
        try {
            currentPrice = jdbcTemplate.queryForObject(priceSql, BigDecimal.class, ticker);
        } catch (Exception e) {
            return 0; // Guard rail: Stock ticker not found in portfolio
        }

        if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 3. Define standard block risk allocation (e.g., Target 4% of cash value per standard trade)
        BigDecimal targetAllocationPct = new BigDecimal("0.0400");
        BigDecimal targetTradeValue = availableCash.multiply(targetAllocationPct); // e.g., $50,000 * 0.04 = $2,000

        // 4. Convert the target dollar value into exact share units
        int baseSellQuantity = targetTradeValue.divide(currentPrice, RoundingMode.DOWN).intValue();

        // 5. Ensure we never return a negative or zero value if price is abnormally high
        return Math.max(baseSellQuantity, 1);
    }
}
