package io.cloudNativeData.sentiment.agent;

import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import  org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import  org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
//import org.springframework.messaging.converter.MessageConverter;

@Configuration
public class RabbitConf {

    @Value("${spring.cloud.stream.bindings.input.destination:stocks.news}")
    private String stockNewsAnalysisExchange;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }



    @Bean
    Publisher<StockNewsAnalysis> publisher(RabbitTemplate template, MessageConverter messageConverter) {

        template.setMessageConverter(messageConverter);

        return news -> {
            template.convertSendAndReceive(stockNewsAnalysisExchange,news.getTicker(),news);
        };
    }
}
