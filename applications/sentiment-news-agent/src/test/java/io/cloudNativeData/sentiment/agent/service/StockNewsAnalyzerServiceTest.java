package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.sentiment.agent.repository.StockNewsAnalysisRepository;
import io.cloudNativeData.sentiment.agent.repository.StockNewsAnalysisSemanticRepository;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockNewsAnalyzerServiceTest {

    private static final NewsParameters newsParameters = JavaBeanGeneratorCreator.of(NewsParameters.class).create();

    @Mock
    private StockAnalysisInference inference;

    @Mock
    private StockNewsAnalysisRepository stockNewsAnalysisRepository;

    @Mock
    private StockNewsAnalysisSemanticRepository stockNewsAnalysisSemanticRepository;


    @Mock
    private StockNewsAnalysisSemanticRepository semanticRepository;

    private final StockNewsAnalysis stockNewsAnalysis = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();

    private StockNewsAnalyzerService subject;
    @Mock
    private StockPrediction mockPrediction;

    @BeforeEach
    void setUp() {
        subject = new StockNewsAnalyzerService(inference,stockNewsAnalysisRepository, semanticRepository);
    }

    @Test
    @DisplayName("Should use prediction from semanticRepository and skip inference when a semantic match exists")
    void shouldReturnCachedPredictionWhenSemanticQuerySucceeds() {
        // Given
        when(semanticRepository.findStockPredictionByRawNews(anyString()))
                .thenReturn(Optional.of(mockPrediction));

        // When
        StockNewsAnalysis result = subject.analyze(newsParameters);

        // Then
        assertNotNull(result);
        assertEquals(mockPrediction, result.getStockPrediction());
        assertEquals(newsParameters.stockTicker(), result.getTicker());

        // Verification: Verify semanticRepository WAS called
        verify(semanticRepository, times(1)).findStockPredictionByRawNews(newsParameters.rawNews());

        // Verification: Verify inference WAS NOT called (crucial for checking conditional branch)
        verify(inference, never()).infer(any());

        // Verification: Verify the final layout was saved
        verify(stockNewsAnalysisRepository, times(1)).save(any(StockNewsAnalysis.class));
    }

    @Test
    @DisplayName("Should fall back to inference service when semanticRepository returns empty")
    void shouldFallbackToInferenceWhenSemanticQueryReturnsEmpty() {
        // Given
        when(semanticRepository.findStockPredictionByRawNews(newsParameters.rawNews()))
                .thenReturn(Optional.empty());
        when(inference.infer(newsParameters)).thenReturn(mockPrediction);

        // When
        StockNewsAnalysis result = subject.analyze(newsParameters);

        // Then
        assertNotNull(result);
        assertEquals(mockPrediction, result.getStockPrediction());

        // Verification: Both repository check and inference should have executed sequentially
        verify(semanticRepository, times(1)).findStockPredictionByRawNews(newsParameters.rawNews());
        verify(inference, times(1)).infer(newsParameters);
        verify(stockNewsAnalysisRepository, times(1)).save(any(StockNewsAnalysis.class));
    }

    @Test
    void apply() {
        var prediction = StockPrediction.builder().build();

        StockNewsAnalysis expected = StockNewsAnalysis
                .builder().id(newsParameters.stockTicker())
                .rawNews(newsParameters.rawNews())
                .ticker(newsParameters.stockTicker())
                .stockPrediction(prediction)
                .build();

        when(inference.infer(newsParameters)).thenReturn(prediction);

        var actual = subject.analyze(newsParameters);

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