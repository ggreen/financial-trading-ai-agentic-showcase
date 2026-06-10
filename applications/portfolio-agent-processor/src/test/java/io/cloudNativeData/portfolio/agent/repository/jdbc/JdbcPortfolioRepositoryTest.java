package io.cloudNativeData.portfolio.agent.repository.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcPortfolioRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    private JdbcPortfolioRepository subject;
    private final String ticker = "NYLA";
    private final String cashSql = "SELECT quantity FROM portfolio.portfolio_positions WHERE asset_type = 'CASH' AND ticker = 'USD'";
    private final String priceSql = "SELECT current_price FROM portfolio.portfolio_positions WHERE asset_type = 'STOCK' AND ticker = ?";

    @BeforeEach
    void setUp() {
        subject = new JdbcPortfolioRepository(jdbcTemplate);
    }

    @Test
    void findTotalPortfolioValue() {

        var expected = BigDecimal.TEN;

        when(jdbcTemplate.queryForObject(anyString(),any(Class.class))).thenReturn(expected);

        var actual = subject.findTotalPortfolioValue();
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void findMaxAllocationPerTrade() {

        var expected = BigDecimal.TWO;

        when(jdbcTemplate.queryForObject(anyString(),any(Class.class))).thenReturn(expected);

        var actual = subject.findMaxAllocationPerTrade();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should calculate exact quantity when math results in a clean whole number")
     void shouldCalculateExactQuantity() {
        // Cash: $50,000 -> 4% Target Allocation = $2,000
        // Price: $100 -> $2,000 / $100 = 20 shares
        when(jdbcTemplate.queryForObject(cashSql, BigDecimal.class)).thenReturn(new BigDecimal("50000.00"));
        when(jdbcTemplate.queryForObject(eq(priceSql), eq(BigDecimal.class), eq(ticker))).thenReturn(new BigDecimal("100.00"));

        int result = subject.findBaseSellQuantity(ticker);

        assertEquals(20, result);
    }

    @Test
    @DisplayName("Should round down when math results in a decimal quantity")
    void shouldRoundDownQuantity() {
        // Cash: $50,000 -> 4% Target Allocation = $2,000
        // Price: $150 -> $2,000 / $150 = 13.333 -> should floor to 13
        when(jdbcTemplate.queryForObject(cashSql, BigDecimal.class)).thenReturn(new BigDecimal("50000.00"));
        when(jdbcTemplate.queryForObject(eq(priceSql), eq(BigDecimal.class), eq(ticker))).thenReturn(new BigDecimal("150.00"));

        int result = subject.findBaseSellQuantity(ticker);

        assertEquals(13, result);
    }

    @Test
    @DisplayName("Should return minimum of 1 when calculated quantity floors to 0")
    void shouldReturnMinimumOfOne() {
        // Cash: $50,000 -> 4% Target Allocation = $2,000
        // Price: $3,000 -> $2,000 / $3,000 = 0.666 -> floors to 0, but Math.max overrides to 1
        when(jdbcTemplate.queryForObject(cashSql, BigDecimal.class)).thenReturn(new BigDecimal("50000.00"));
        when(jdbcTemplate.queryForObject(eq(priceSql), eq(BigDecimal.class), eq(ticker))).thenReturn(new BigDecimal("3000.00"));

        int result = subject.findBaseSellQuantity(ticker);

        assertEquals(1, result);
    }
}