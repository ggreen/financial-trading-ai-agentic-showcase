package io.cloudNativeData.trading.watch;


import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import lombok.Builder;

import java.util.List;

@Builder
public record StockPriceMovingAverageWatchList(Iterable<StockPriceMovingAverage> stockPriceMovingAverages,
                                               String buyQuery,
                                               String sellQuery) {
}
