package io.cloudNativeData.trading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockTradeAdvice {
    private String id;
    private TradePrediction adviceAction;
    private double price;
    private int quantity;
}
