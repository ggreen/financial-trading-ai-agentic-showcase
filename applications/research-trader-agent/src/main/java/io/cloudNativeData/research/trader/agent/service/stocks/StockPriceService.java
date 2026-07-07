package io.cloudNativeData.research.trader.agent.service.stocks;

import io.cloudNativeData.research.trader.agent.properties.StockApiProperties;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class StockPriceService {


    private final RestClient restClient;

    private final CircuitBreaker readCircuitBreaker;

    private final GetStockFallbackService fallbackService;

    // Injecting RestClient.Builder is best practice for configuration flexibility
    public StockPriceService(RestClient.Builder restClientBuilder, StockApiProperties stockApiProperties,
                             CircuitBreaker readCircuitBreaker,
                             GetStockFallbackService fallbackService) {
        log.info("Stock Price service initializing... properties: {}",
                stockApiProperties);
        this.restClient = restClientBuilder
                .baseUrl(stockApiProperties.getStockUrl())
                .build();

        this.readCircuitBreaker = readCircuitBreaker;
        this.fallbackService = fallbackService;
    }


//    @Cacheable("StockPrice")
    public StockPriceDto getCurrentStockPrice(String ticker) {
        // Wrap the external network call with the circuit breaker
        return this.readCircuitBreaker.run(
                () -> this.restClient.get()
                        .uri("?ticker={ticker}", ticker)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(StockPriceDto.class),
                throwable -> handleFallback(ticker, throwable)
        );
    }

    private StockPriceDto handleFallback(String ticker, Throwable throwable) {
        log.trace("Circuit breaker triggered or request failed for ticker {}. Reason: {}",
                ticker, throwable.getMessage(), throwable);
        // Assuming your fallbackService has a method to fetch a fallback stock price
        return this.fallbackService.getCurrentStockPrice(ticker);
    }
}