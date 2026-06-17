package io.cloudNativeData.research.trader.agent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.stock.api.service")
@Data
public class StockApiProperties {
    private String stockUrl;
    private String startupStockTicker;
}