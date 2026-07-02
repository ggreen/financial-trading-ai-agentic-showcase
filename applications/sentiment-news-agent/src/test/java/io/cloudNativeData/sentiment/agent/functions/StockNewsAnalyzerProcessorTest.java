package io.cloudNativeData.sentiment.agent.functions;

import io.cloudNativeData.sentiment.agent.functions.processor.StockNewsAnalyzerProcessor;
import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockNewsAnalyzerProcessorTest {

    private static final NewsParameters rawNews = JavaBeanGeneratorCreator.of(NewsParameters.class).create();


    @Mock
    private StockNewsAnalyzerService service;

    private StockNewsAnalyzerProcessor subject;

    @BeforeEach
    void setUp() {
        subject = new StockNewsAnalyzerProcessor(service);
    }

    @Test
    void apply() {
        var prediction = StockPrediction.builder().build();

        StockNewsAnalysis expected = StockNewsAnalysis
                .builder().id(rawNews.stockTicker())
                .rawNews(rawNews.rawNews())
                .ticker(rawNews.stockTicker())
                .stockPrediction(prediction)
                .build();

        when(service.analyze(any())).thenReturn(expected);

        var actual = subject.apply(rawNews);
        assertThat(actual).isEqualTo(expected);
    }
}