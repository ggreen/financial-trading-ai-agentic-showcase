package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;

@FunctionalInterface
public interface PortfolioAnalyticsService {
    PortfolioQueryRequests askAnalytics(String question);
}
