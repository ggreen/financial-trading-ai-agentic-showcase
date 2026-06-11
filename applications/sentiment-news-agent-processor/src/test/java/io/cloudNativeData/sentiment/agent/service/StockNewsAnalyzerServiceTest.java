package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class StockNewsAnalyzerServiceTest {


    private static final NewsParameters rawNews = JavaBeanGeneratorCreator.of(NewsParameters.class).create();


    @Mock
    private StockAnalysisInference inference;

    private StockNewsAnalyzerService subject;

    @BeforeEach
    void setUp() {
        subject = new StockNewsAnalyzerService(inference);
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

        when(inference.infer(rawNews)).thenReturn(prediction);

        var actual = subject.analyze(rawNews);
        assertThat(actual).isEqualTo(expected);
    }
}