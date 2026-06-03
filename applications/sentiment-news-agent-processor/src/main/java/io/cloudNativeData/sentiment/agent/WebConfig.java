package io.cloudNativeData.sentiment.agent;

import io.cloudNativeData.trading.StockTradeAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class WebConfig {

    @Bean
    Function<String, String> ping()
    {
        return ping -> ping;

    }
}
