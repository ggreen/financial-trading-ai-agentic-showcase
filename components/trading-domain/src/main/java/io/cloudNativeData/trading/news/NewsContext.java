package io.cloudNativeData.trading.news;


import io.cloudNativeData.trading.StockPrediction;
import lombok.Builder;

@Builder
public record NewsContext(String rawNews, StockPrediction stockPrediction) {
}
