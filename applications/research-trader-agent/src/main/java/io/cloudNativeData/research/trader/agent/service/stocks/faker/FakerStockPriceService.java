package io.cloudNativeData.research.trader.agent.service.stocks.faker;

import io.cloudNativeData.research.trader.agent.properties.StockPriceFakerConfig;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.service.stocks.GetStockFallbackService;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Digits;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Locale;

@Service
@Slf4j
public class FakerStockPriceService implements GetStockFallbackService {

    private final Digits digits = new Digits();
    private final String exchange;
    private final int lowVolume;
    private final int highVolume;
    private final StockPricingExecution stockPricingExecution;

    public FakerStockPriceService(StockPriceFakerConfig config, StockPricingExecution stockPricingExecution) {
        this.exchange = config.getExchange();
        this.lowVolume = config.getLowVolume();
        this.highVolume = config.getHighVolume();
        this.stockPricingExecution = stockPricingExecution;
    }


    @Override
    public StockPriceDto getCurrentStockPrice(String ticker) {

        log.info("FakerStockPriceService.getCurrentStockPrice(ticker={})", ticker);
        var price = stockPricingExecution.calculateMovingAverage200(ticker);

        log.info("ticker={}, price={}", ticker,price);

        long volume = digits.generateInteger(lowVolume,highVolume);

        return StockPriceDto.builder()
                .ticker(ticker)
                .name(ticker)
                .price(price.doubleValue())
                .currency(Currency.getInstance(Locale.US).getCurrencyCode())
                .updated(System.currentTimeMillis())
                .exchange(exchange)
                .volume(volume)
                .build();
    }
}
