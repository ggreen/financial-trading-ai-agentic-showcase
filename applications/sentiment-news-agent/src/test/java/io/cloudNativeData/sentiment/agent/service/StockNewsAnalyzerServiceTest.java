package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.sentiment.agent.repository.StockNewsAnalysisRepository;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockNewsAnalyzerServiceTest {

    private static final NewsParameters rawNews = JavaBeanGeneratorCreator.of(NewsParameters.class).create();

    @Mock
    private StockAnalysisInference inference;

    @Mock
    private StockNewsAnalysisRepository stockNewsAnalysisRepository;


    private StockNewsAnalyzerService subject;
    private final StockNewsAnalysis stockNewsAnalysis = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();

    @BeforeEach
    void setUp() {
        subject = new StockNewsAnalyzerService(inference,stockNewsAnalysisRepository);
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

        verify(stockNewsAnalysisRepository).save(any());
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void findAll() {

        var expected = List.of(stockNewsAnalysis);
        when(stockNewsAnalysisRepository.findAll()).thenReturn(expected);

        var actual = subject.findAllNews();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllOrderedByTicker() {

        StockNewsAnalysis news1 = StockNewsAnalysis.builder().ticker("Z").build();
        StockNewsAnalysis news2 = StockNewsAnalysis.builder().ticker("A").build();
        StockNewsAnalysis news3 = StockNewsAnalysis.builder().ticker("C").build();
        var expected = List.of(news1,news2,news3);
        when(stockNewsAnalysisRepository.findAll()).thenReturn(expected);

        var actual = subject.findAllNews();

        assertThat(actual).isNotNull();

        Iterator<StockNewsAnalysis> iterator = actual.iterator();

        assertThat(iterator.next()).isEqualTo(news2);
        assertThat(iterator.next()).isEqualTo(news3);
        assertThat(iterator.next()).isEqualTo(news1);
    }
}