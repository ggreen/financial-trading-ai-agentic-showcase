package io.cloudNativeData.trading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 'sentiment' (BULLISH/BEARISH/NEUTRAL),
 * and 'confidence' (0.0-1.0)."
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPrediction {
    private MarketSentiment marketSentiment;
    private BigDecimal confidence;
}
