package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.service.TradeAdviceService;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeAdvice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SuggestTradeAdviceProcessor implements Function<StockNewsAnalysis, TradeAdvice> {

    private final TradeAdviceService service;

    @Override
    public TradeAdvice apply(StockNewsAnalysis stockNewsAnalysis) {
        return service.recommend(stockNewsAnalysis);
    }
}
