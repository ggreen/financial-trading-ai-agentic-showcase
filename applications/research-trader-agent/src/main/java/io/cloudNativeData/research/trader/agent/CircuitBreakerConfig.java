package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.research.trader.agent.properties.StockPriceFakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties(StockPriceFakerConfig.class)
public class CircuitBreakerConfig {

//    private final Digits digits = new Digits();

    @Bean
    CircuitBreaker stockPriceCircuitBreaker(CircuitBreakerFactory<?,?> cbFactory) {
        return cbFactory.create("stockPriceCircuitBreaker");
    }

//    @Bean
//    GetStockFallbackService getStockFallbackService() {
//
//        return ticker  ->{
//            var price = digits.generateDouble(3.2,100.00);
//            var dto = StockPriceDto.builder().ticker(ticker).build();
//
//            log.info("Generate fake for stockPriceCircuitBreaker:{}",dto);
//            return dto;
//        };
//    }

}
