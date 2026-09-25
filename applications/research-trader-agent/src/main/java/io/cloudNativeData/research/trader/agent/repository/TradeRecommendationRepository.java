package io.cloudNativeData.research.trader.agent.repository;

import io.cloudNativeData.trading.TradeRecommendation;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.stereotype.Repository;

/**
 * Trade Recommendation Repository
 * @author gregory green
 */
@Repository
public interface TradeRecommendationRepository
        extends GemfireRepository<TradeRecommendation, String> {
}
