package io.cloudNativeData.trading.risk;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.TradeAction;

public record TradeRiskParameters(TradeAction tradeAction,
                                  int quantity,
                                  String ticker,
                                  StockPrediction prediction,
                                  String rawNews
                                  ) {
}
