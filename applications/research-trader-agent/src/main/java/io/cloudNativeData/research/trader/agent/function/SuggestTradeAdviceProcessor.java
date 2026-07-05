package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuggestTradeAdviceProcessor implements Function<StockNewsAnalysis, TradeRecommendation> {

    private final ResearchTraderService service;

    @Override
    public TradeRecommendation apply(StockNewsAnalysis stockNewsAnalysis) {
        log.info("Suggest trade advice processor: {}",stockNewsAnalysis);
        return service.recommend(stockNewsAnalysis);
    }
}
