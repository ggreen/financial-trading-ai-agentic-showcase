package io.cloudNativeData.trading.pricing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceDto {

    /*
    {"ticker": "AAPL",
    "name": "Apple Inc.", "price": 292.4, "exchange": "NASDAQ", "updated": 1781530273, "currency": "USD", "volume": 1501580}%
     */

    private String ticker;
    private String name;
    private double price;
    private String exchange;
    private long updated;
    private String currency;
    private long volume;

}
