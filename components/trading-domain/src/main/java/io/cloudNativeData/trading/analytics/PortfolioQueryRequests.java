package io.cloudNativeData.trading.analytics;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record PortfolioQueryRequests(List<Map<String, Object>> results, String sql) {


}
