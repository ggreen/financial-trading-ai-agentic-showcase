package io.cloudNativeData.research.trader.agent.ai;

import io.cloudNativeData.trading.StockSummary;
import io.cloudNativeData.trading.StockTradeAdvice;

@FunctionalInterface
public interface TradeSuggestionInference {
    StockTradeAdvice recommend(StockSummary stockSummary);
}
