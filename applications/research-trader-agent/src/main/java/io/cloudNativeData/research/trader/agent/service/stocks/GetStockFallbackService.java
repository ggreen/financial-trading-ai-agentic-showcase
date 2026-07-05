package io.cloudNativeData.research.trader.agent.service.stocks;

import io.cloudNativeData.trading.pricing.StockPriceDto;

@FunctionalInterface
public interface GetStockFallbackService {

    public StockPriceDto getCurrentStockPrice(String ticker);
}
