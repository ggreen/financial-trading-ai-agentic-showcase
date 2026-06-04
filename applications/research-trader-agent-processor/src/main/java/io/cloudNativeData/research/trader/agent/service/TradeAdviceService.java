package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradeSuggestionInference;
import io.cloudNativeData.research.trader.agent.repository.StockRepository;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.StockSummary;
import io.cloudNativeData.trading.StockTradeAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeAdviceService {

    private final TradeSuggestionInference inference;
    private final StockRepository repository;

    public StockTradeAdvice recommend(StockNewsAnalysis stockNewsAnalysis) {

        var movingAverage200 = repository.calculateMovingAverage200(stockNewsAnalysis.getId());
        var summary200 = StockSummary.builder()
                .news(stockNewsAnalysis)
                .movingAverage200(movingAverage200)
                .build();

        var advice = inference.recommend(summary200);

        log.info("advice: {}", advice);
        return advice;
    }
}
