package io.cloudNativeData.trading;

import io.cloudNativeData.trading.risk.RiskPrediction;
import lombok.Builder;

@Builder
public record PortfolioTradeProposal(String id,
                                     int quantity,
                                     TradeRecommendation tradeRecommendation,
                                     RiskPrediction riskPrediction)  {
}
