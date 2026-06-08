package io.cloudNativeData.portfolio.agent.function;

import io.cloudNativeData.portfolio.agent.service.ProposeTradeService;
import io.cloudNativeData.trading.PortfolioTradeGeneration;
import io.cloudNativeData.trading.TradeGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ProposeTradeFunction implements Function<TradeGeneration, PortfolioTradeGeneration> {

    private final ProposeTradeService service;

    @Override
    public PortfolioTradeGeneration apply(TradeGeneration tradeGeneration) {
        return service.propose(tradeGeneration);
    }
}
