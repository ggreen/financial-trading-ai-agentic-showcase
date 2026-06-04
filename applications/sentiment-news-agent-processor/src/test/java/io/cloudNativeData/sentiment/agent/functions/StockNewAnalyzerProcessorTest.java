package io.cloudNativeData.sentiment.agent.functions;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.sentiment.agent.functions.processor.StockNewAnalyzerProcessor;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockNewAnalyzerProcessorTest {

    private static final NewsParameters rawNews = JavaBeanGeneratorCreator.of(NewsParameters.class).create();


    @Mock
    private StockAnalysisInference inference;

    private StockNewAnalyzerProcessor subject;

    @BeforeEach
    void setUp() {
        subject = new StockNewAnalyzerProcessor(inference);
    }

    @Test
    void apply() {
        var prediction = StockPrediction.builder().build();

        StockNewsAnalysis expected = StockNewsAnalysis
                .builder().id(rawNews.stockTicker())
                .rawNews(rawNews.rawNews())
                .prediction(prediction)
                .build();

        when(inference.infer(rawNews)).thenReturn(prediction);

        var actual = subject.apply(rawNews);
        assertThat(actual).isEqualTo(expected);
    }
}