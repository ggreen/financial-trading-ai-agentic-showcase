package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.service.TradeAdviceService;
import io.cloudNativeData.trading.news.StockNewsGeneration;
import io.cloudNativeData.trading.TradeGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SuggestTradeAdviceProcessor implements Function<StockNewsGeneration, TradeGeneration> {

    private final TradeAdviceService service;

    @Override
    public TradeGeneration apply(StockNewsGeneration stockNewsGeneration) {
        return service.recommend(stockNewsGeneration);
    }
}
