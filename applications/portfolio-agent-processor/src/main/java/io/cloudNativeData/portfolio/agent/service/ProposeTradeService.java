package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ProposeTradeService {
    private final PortfolioRepository repository;

    public PortfolioTradeProposal propose(TradeRecommendation trade) {
        var quantity =
                switch (trade.getTradePrediction().getAdviceAction()) {
                    case BUY -> determineBuyQuantity(trade);
                    case SELL -> determineSellQuantity(trade);
                    default -> 0;
                };

        return PortfolioTradeProposal.builder()
                .quantity(quantity)
                .tradeRecommendation(trade)
                .id(trade.getId())
                .build();
    }

    /*
     Implement determine stock trade sell quantity
        private String id;
    private TradePrediction tradePrediction;
    private StockNewsGeneration stockNewsGeneration;
    private BigDecimal price;
        private StockPrediction prediction_bull_or_bearish ;
    private String rawNews;
     */
    private int determineSellQuantity(TradeRecommendation trade) {
        // 1. Sanity check: If trade data or prediction is null, do nothing.
        if (trade == null
                || trade.getStockNewsGeneration().getPrediction() == null
                || trade.getStockNewsGeneration().getPrediction().getMarketSentiment() == null) {
            return 0;
        }

        var bullOrBearish = trade.getStockNewsGeneration().getPrediction().getMarketSentiment();

        var ticker = trade.getStockNewsGeneration().getTicker();
        // 2. If the outlook is Bullish, we hold.
        if (MarketSentiment.BULLISH.equals(bullOrBearish)) {
            return 0;
        }

        // 3. If the outlook is Bearish, calculate sell quantity based on risk factors
        if ( MarketSentiment.BEARISH.equals(bullOrBearish)) {
            int baseSellQuantity = repository.findBaseSellQuantity(ticker); // Define your baseline share block size

            // Extract confidence or risk multiplier if available in tradePrediction
            double confidenceMultiplier = 1.0;
            if (trade.getTradePrediction() != null) {
                // Assuming getConfidence() returns a value between 0.0 and 1.0
                confidenceMultiplier = trade.getTradePrediction().getTradeConfidence();
            }

            // Scale the sell quantity dynamically based on signal strength
            int finalSellQuantity = (int) (baseSellQuantity * confidenceMultiplier);

            // Optional: Caps to ensure you don't over-sell past standard risk limits
            int maxSellLimit = repository.findMaxSellLimit(trade.getStockNewsGeneration().getTicker());
            return Math.min(finalSellQuantity, maxSellLimit);
        }

        return 0;
    }

    public int determineBuyQuantity(TradeRecommendation advice) {

        var stockMarketPrice = advice.getTradePrediction().getPrice();
        if (stockMarketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 2. Calculate maximum cash allowed for this single trade
        var maxTradeCapital = this.repository.findTotalPortfolioValue().multiply(
                repository.findMaxAllocationPerTrade());

        // 3. Scale the capital based on the AI/News conviction score
        // If sentiment is 0.5, we only use 50% of our maximum allowed capital
        var allocatedCapital = maxTradeCapital.multiply(advice.getStockNewsGeneration()
                .getPrediction().getConfidence());

        // 4. Calculate share quantity
        var targetQuantity = allocatedCapital.divide(stockMarketPrice,
                4, RoundingMode.HALF_UP
        );

        // 5. Return as a whole integer (rounding down to be safe)
        return targetQuantity.intValue();
    }
}
