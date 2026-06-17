package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.properties.StockApiProperties;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class StockPriceService {


    private final RestClient restClient;

    // Injecting RestClient.Builder is best practice for configuration flexibility
    public StockPriceService(RestClient.Builder restClientBuilder, StockApiProperties stockApiProperties) {
        log.info("Stock Price service initializing... properties: {}",
                stockApiProperties);
        this.restClient = restClientBuilder
                .baseUrl(stockApiProperties.getStockUrl())
                .build();
    }

    @Cacheable("StockPrice")
    public StockPriceDto getCurrentStockPrice(String ticker) {
        return this.restClient.get()
                .uri("?ticker={ticker}", ticker) // Spring safely binds the ticker variable here
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(StockPriceDto.class);
    }
}
