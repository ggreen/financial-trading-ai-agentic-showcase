package io.cloudNativeData.sentiment.agent.ai;

import io.cloudNativeData.trading.StockNewsAnalysis;

@FunctionalInterface
public interface StockAnalysisInference {
    StockNewsAnalysis infer(String rawNews);
}
