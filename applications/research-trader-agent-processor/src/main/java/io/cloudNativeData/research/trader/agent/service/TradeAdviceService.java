package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.trading.news.StockNewsGeneration;
import io.cloudNativeData.trading.TradeParameters;
import io.cloudNativeData.trading.TradeGeneration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeAdviceService {

    private final TradePredictionInference inference;
    private final StockPricingExecution repository;

    public TradeGeneration recommend(StockNewsGeneration stockNewsGeneration) {

        var movingAverage200 = repository.calculateMovingAverage200(new String[]{stockNewsGeneration.getId()});
        var summary200 = TradeParameters.builder()
                .prediction(stockNewsGeneration.getPrediction())
                .movingAverage200(movingAverage200)
                .build();

        var tradePrediction = inference.recommend(summary200);

        log.info("predication: {}", tradePrediction);
        return TradeGeneration
                .builder()
                .id(stockNewsGeneration.getId())
                .tradePrediction(tradePrediction)
                .stockNewsGeneration(stockNewsGeneration)
                .build();
    }
}
