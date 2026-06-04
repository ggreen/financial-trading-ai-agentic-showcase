package io.cloudNativeData.trading.news;

import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.StockPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * : 'ticker',
 * 'sentiment' (BULLISH/BEARISH/NEUTRAL),
 * and 'confidence' (0.0-1.0)."
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockNewsAnalysis {
    private String id; //stockId
    private StockPrediction prediction;
    private String rawNews;
}
