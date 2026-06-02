package io.cloudNativeData.trading;

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
    private MarketSentiment marketSentiment;
    private double confidence;
    private String rawNews;
}
