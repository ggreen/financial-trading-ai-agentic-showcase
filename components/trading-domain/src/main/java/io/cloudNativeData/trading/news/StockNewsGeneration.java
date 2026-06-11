package io.cloudNativeData.trading.news;

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
public class StockNewsGeneration {
    private String id;
    private String ticker;
    private StockPrediction stockPrediction;
    private String rawNews;
}
