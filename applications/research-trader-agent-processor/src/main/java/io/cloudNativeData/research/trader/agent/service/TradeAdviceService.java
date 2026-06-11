package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeParameters;
import io.cloudNativeData.trading.TradeRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeAdviceService {

    private final TradePredictionInference inference;
    private final StockPricingExecution repository;

    public TradeRecommendation recommend(StockNewsAnalysis stockNewsAnalysis) {

        var movingAverage200 = repository.calculateMovingAverage200(new String[]{stockNewsAnalysis.getId()});
        var summary200 = TradeParameters.builder()
                .prediction(stockNewsAnalysis.getStockPrediction())
                .movingAverage200(movingAverage200)
                .build();

        var tradePrediction = inference.recommend(summary200);

        log.info("predication: {}", tradePrediction);
        return TradeRecommendation
                .builder()
                .id(stockNewsAnalysis.getId())
                .tradePrediction(tradePrediction)
                .stockNewsAnalysis(stockNewsAnalysis)
                .build();
    }
}
