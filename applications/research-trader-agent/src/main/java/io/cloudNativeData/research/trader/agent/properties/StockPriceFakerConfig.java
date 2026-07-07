package io.cloudNativeData.research.trader.agent.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Currency;

@ConfigurationProperties(prefix = "stock.faker")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceFakerConfig{

    private String exchange;
    private int lowVolume;
    private int highVolume;
    private String currency;
}