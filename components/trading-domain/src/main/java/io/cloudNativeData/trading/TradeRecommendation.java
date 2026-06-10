package io.cloudNativeData.trading;

import io.cloudNativeData.trading.news.StockNewsGeneration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeRecommendation {
    private String id;
    private TradePrediction tradePrediction;
    private StockNewsGeneration stockNewsGeneration;

}
