package io.cloudNativeData.trading;

import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeRecommendation {
    private String id;
    private TradePrediction tradePrediction;
    private StockNewsAnalysis stockNewsAnalysis;
}
