package io.cloudNativeData.trading.risk;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.TradeAction;
import lombok.Builder;

@Builder
public record TradeRiskParameters(TradeAction tradeAction,
                                  int quantity,
                                  String ticker,
                                  StockPrediction stockPrediction,
                                  String newsSummary
                                  ) {
}
