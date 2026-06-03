package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.ai.TradeSuggestionInference;
import io.cloudNativeData.research.trader.agent.service.TradeAdviceService;
import io.cloudNativeData.trading.StockNewsAnalysis;
import io.cloudNativeData.trading.StockTradeAdvice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SuggestTradeAdviceFunction implements Function<StockNewsAnalysis, StockTradeAdvice> {

    private final TradeAdviceService service;

    @Override
    public StockTradeAdvice apply(StockNewsAnalysis stockNewsAnalysis) {
        return service.recommend(stockNewsAnalysis);
    }
}
