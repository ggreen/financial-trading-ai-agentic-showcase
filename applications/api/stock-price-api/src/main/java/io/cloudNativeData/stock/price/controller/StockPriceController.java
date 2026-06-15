package io.cloudNativeData.stock.price.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class StockPriceController {

    private final WebClient webClient;

    public StockPriceController(WebClient.Builder webClientBuilder,
                                 @Value("${API_NINJAS_KEY}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.api-ninjas.com/v1")
                .defaultHeader("X-Api-Key", apiKey)
                .build();
    }

    /**
     * 1. Stock price endpoint handling business logic.
     * The Gateway filters hit this app, validate rate limits, and proxy right back to here.
     */
    @GetMapping("/v1/stockprice")
    public Mono<String> getStockPrice(@RequestParam String ticker) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stockprice")
                        .queryParam("ticker", ticker)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
