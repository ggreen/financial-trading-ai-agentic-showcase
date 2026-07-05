package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.research.trader.agent.properties.StockApiProperties;
import io.cloudNativeData.research.trader.agent.service.stocks.StockPriceService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {


    @Bean
    ApplicationRunner applicationRunner(StockApiProperties stockApiProperties, StockPriceService stockPriceService)
    {
        return args -> {
            stockPriceService.getCurrentStockPrice(stockApiProperties.getStartupStockTicker());
        };

    }
}
