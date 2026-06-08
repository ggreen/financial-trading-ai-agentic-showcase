package io.cloudNativeData.trading;

import lombok.Builder;

@Builder
public record PortfolioTradeGeneration(String id,
                                       int quantity,
                                       TradeGeneration tradeGeneration)  {
}
