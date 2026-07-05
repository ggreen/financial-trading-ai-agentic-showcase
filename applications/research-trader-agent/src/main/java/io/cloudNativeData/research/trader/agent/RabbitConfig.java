package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    @Value("${spring.cloud.stream.bindings.suggestTradeAdviceProcessor-out-0.destination}")
    private String tradeRecommendationExchange;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    Publisher<TradeRecommendation> publisherTradeRecommendation(RabbitTemplate template, MessageConverter messageConverter) {

        template.setMessageConverter(messageConverter);

        return tradeRecommendation -> {
            log.info("RabbitMQ stock news analysis received : {}", tradeRecommendation);
            log.info("Sending to exchange : {}", tradeRecommendationExchange);

            template.convertSendAndReceive(tradeRecommendationExchange,tradeRecommendation.getId(),tradeRecommendation);
        };
    }
}
