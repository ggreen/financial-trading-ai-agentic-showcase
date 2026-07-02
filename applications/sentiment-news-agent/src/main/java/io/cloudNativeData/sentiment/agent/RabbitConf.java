package io.cloudNativeData.sentiment.agent;

import com.rabbitmq.stream.Environment;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import  org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import  org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.support.StreamAdmin;
//import org.springframework.messaging.converter.MessageConverter;

@Configuration
@Slf4j
public class RabbitConf {

    @Value("${spring.cloud.stream.bindings.input.destination}")
    private String outboundNewsStream;

    @Value("${spring.cloud.stream.bindings.output.destination}")
    private String stockNewsAnalysisExchange;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }



    @Bean
    StreamAdmin streamAdmin(Environment env) {
        log.info("Declaring stream: {}", outboundNewsStream);

        return new StreamAdmin(env, sc -> {
            sc.stream(outboundNewsStream).create();
        });
    }

    @Bean
    Publisher<StockNewsAnalysis> publisher(RabbitTemplate template, MessageConverter messageConverter) {



        template.setMessageConverter(messageConverter);


        return news -> {
            log.info("RabbitMQ stock news analysis received : {}", news);
            log.info("Sending to exchange : {}", stockNewsAnalysisExchange);

            template.convertSendAndReceive(stockNewsAnalysisExchange,news.getTicker(),news);
        };
    }
}
