package io.cloudNativeData.sentiment.agent.ai;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;

@FunctionalInterface
public interface StockAnalysisInference {
    StockPrediction infer(NewsParameters rawNews);
}
