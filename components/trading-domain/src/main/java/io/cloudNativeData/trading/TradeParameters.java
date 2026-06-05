package io.cloudNativeData.trading;

import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeParameters {

    private StockPrediction prediction;
    private BigDecimal movingAverage200;
}
