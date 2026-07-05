package io.cloudNativeData.research.trader.agent.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stock.listener")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockListenerConfig {

    private String sellerName;
    private String sellerQuery;
    private String buyerName;
    private String buyerQuery;
}