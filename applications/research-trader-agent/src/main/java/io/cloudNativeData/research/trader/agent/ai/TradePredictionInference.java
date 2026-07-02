package io.cloudNativeData.research.trader.agent.ai;

import io.cloudNativeData.trading.TradeParameters;
import io.cloudNativeData.trading.TradePrediction;

@FunctionalInterface
public interface TradePredictionInference {
    TradePrediction recommend(TradeParameters tradeParameters);
}
