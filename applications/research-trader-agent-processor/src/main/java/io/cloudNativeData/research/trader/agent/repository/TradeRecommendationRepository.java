package io.cloudNativeData.research.trader.agent.repository;

import io.cloudNativeData.trading.TradeRecommendation;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRecommendationRepository
        extends GemfireRepository<TradeRecommendation, String> {
}
