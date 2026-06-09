package io.cloudNativeData.portfolio.agent.repository.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcPortfolioRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    private JdbcPortfolioRepository subject;

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
}