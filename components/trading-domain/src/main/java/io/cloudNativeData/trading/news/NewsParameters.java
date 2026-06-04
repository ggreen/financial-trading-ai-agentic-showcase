package io.cloudNativeData.trading.news;

import lombok.Builder;

@Builder
public record NewsParameters(String stockTicker, String rawNews) {
}
