package io.cloudNativeData.trading;

import lombok.Builder;

@Builder
public record PortfolioTradeProposal(String id,
                                     int quantity,
                                     TradeRecommendation tradeRecommendation)  {
}
