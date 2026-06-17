package io.cloudNativeData.sentiment.agent;

import io.cloudNativeData.trading.news.NewsParameters;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Digits;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class GenerateFakeNewsConfig {

    private final Digits digits = new Digits();
    private final String[] stocks = {"ACME_WELL", "ACME_SPORT","ACME_INS","ACME_PHARMA","ACME_TELCO"};
    private final String[] industries = {"Health", "Sport Entertainment","Insurance","Pharmaceutical Life Sciences","Telecommunications"};
    private final String prompt = """
            Generate news about company stock symbol: {stock} in the industry:{industry} that an stock analyst would analyze to 
            determine if you should buy or sell the stock.
            """;

    @Bean
    Supplier<NewsParameters> generateNews(ChatClient.Builder builder){

        return () -> {
            var index = digits.generateInteger(0, stocks.length - 1);

            var stock = stocks[index];
            var industry = industries[index];

            var rawNews = builder.defaultSystem("You are a financial news writer")
                    .build()
                    .prompt()
                    .user(u -> u.text(prompt)
                            .params(Map.of("stock", stock,
                                    "industry", industry)))
                    .call()
                    .content();

            log.info("News information for stock: {}, industry: {} is {}", stock, industry,rawNews);
            return NewsParameters.builder().rawNews(rawNews).stockTicker(stock).build();
        };
    }
}
