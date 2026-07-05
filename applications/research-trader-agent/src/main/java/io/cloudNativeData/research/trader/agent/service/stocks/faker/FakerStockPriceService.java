package io.cloudNativeData.research.trader.agent.service.stocks.faker;

import io.cloudNativeData.research.trader.agent.properties.StockPriceFakerConfig;
import io.cloudNativeData.research.trader.agent.service.stocks.GetStockFallbackService;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.util.Digits;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Locale;

@Service
public class FakerStockPriceService implements GetStockFallbackService {

    private final Digits digits = new Digits();
    private final String exchange;
    private final Double lowPrice;
    private final Double highPrice;
    private final int lowVolume;
    private final int highVolume;

    public FakerStockPriceService(StockPriceFakerConfig config) {
        this.exchange = config.getExchange();
        this.lowPrice = config.getLowPrice();
        this.highPrice = config.getHighPrice();
        this.lowVolume = config.getLowVolume();
        this.highVolume = config.getHighVolume();
    }


    @Override
    public StockPriceDto getCurrentStockPrice(String ticker) {

        var price = digits.generateDouble(lowPrice,highPrice);

        long volume = digits.generateInteger(lowVolume,highVolume);

        return StockPriceDto.builder()
                .ticker(ticker)
                .name(ticker)
                .price(price)
                .currency(Currency.getInstance(Locale.US).getCurrencyCode())
                .updated(System.currentTimeMillis())
                .exchange(exchange)
                .volume(volume)
                .build();
    }
}
