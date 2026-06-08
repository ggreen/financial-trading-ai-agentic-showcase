package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import io.cloudNativeData.trading.PortfolioTradeGeneration;
import io.cloudNativeData.trading.TradeGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static io.cloudNativeData.trading.TradeAction.BUY;

@Service
@RequiredArgsConstructor
public class ProposeTradeService {
    private final PortfolioRepository repository;

    public PortfolioTradeGeneration propose(TradeGeneration trade) {
        var quantity =
                switch (trade.getTradePrediction().getAdviceAction()) {
                    case BUY -> determineBuy(trade);
                    default -> 0;
                };

        return PortfolioTradeGeneration.builder()
                .quantity(quantity)
                .tradeGeneration(trade)
                .id(trade.getId())
                .build();
    }

    public int determineBuy(TradeGeneration advice) {

        var stockMarketPrice = advice.getTradePrediction().getPrice();
        if (stockMarketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 2. Calculate maximum cash allowed for this single trade
        BigDecimal maxTradeCapital = this.repository.findTotalPortfolioValue().multiply(
                repository.findMaxAllocationPerTrade());

        // 3. Scale the capital based on the AI/News conviction score
        // If sentiment is 0.5, we only use 50% of our maximum allowed capital
        BigDecimal allocatedCapital = maxTradeCapital.multiply(advice.getStockNewsGeneration()
                .getPrediction().getConfidence());

        // 4. Calculate share quantity
        BigDecimal targetQuantity = allocatedCapital.divide(stockMarketPrice);

        // 5. Return as a whole integer (rounding down to be safe)
        return targetQuantity.intValue();
    }
}
