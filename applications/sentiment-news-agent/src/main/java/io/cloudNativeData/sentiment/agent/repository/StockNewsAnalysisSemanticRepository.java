package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.StockPrediction;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StockNewsAnalysisSemanticRepository {

    public Optional<StockPrediction> findStockPredictionByRawNews(String rawNews){
        return Optional.empty();
    }
}
