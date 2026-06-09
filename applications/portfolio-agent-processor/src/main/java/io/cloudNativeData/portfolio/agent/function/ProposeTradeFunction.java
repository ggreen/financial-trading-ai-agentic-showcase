package io.cloudNativeData.portfolio.agent.function;

import io.cloudNativeData.portfolio.agent.service.ProposeTradeService;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ProposeTradeFunction implements Function<TradeRecommendation, PortfolioTradeProposal> {

    private final ProposeTradeService service;

    @Override
    public PortfolioTradeProposal apply(TradeRecommendation tradeRecommendation) {
        return service.propose(tradeRecommendation);
    }
}
