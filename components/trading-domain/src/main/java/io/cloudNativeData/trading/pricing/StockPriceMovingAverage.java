package io.cloudNativeData.trading.pricing;

import io.cloudNativeData.trading.MarketSentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPriceMovingAverage {
    private String id; //ticker
    private double movingAverage200;
    private StockPriceDto stockPrice;
    private String marketSentiment;
}
