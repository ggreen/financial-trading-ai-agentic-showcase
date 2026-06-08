package io.cloudNativeData.portfolio.agent;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;

@Configuration
@EnableJpaRepositories
public class DbConfig {

    @Bean
    PortfolioRepository portfolioRepository()
    {
        return new PortfolioRepository() {
            @Override
            public BigDecimal findTotalPortfolioValue() {
                return null;
            }

            @Override
            public BigDecimal findMaxAllocationPerTrade() {
                return null;
            }
        };
    }
}
