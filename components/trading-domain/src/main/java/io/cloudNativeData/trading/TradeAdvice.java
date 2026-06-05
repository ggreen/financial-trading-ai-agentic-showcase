package io.cloudNativeData.trading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeAdvice {
    private String id;
    private TradePrediction tradePrediction;

}
