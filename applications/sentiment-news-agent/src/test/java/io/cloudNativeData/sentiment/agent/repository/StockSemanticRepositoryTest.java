package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsContext;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockSemanticRepositoryTest {


    @Mock
    private VectorStore vectorStore;

    @Mock
    private NewsContextRepository newsContextRepository;

    private StockSemanticRepository subject;
    // Given
    private final NewsContext mockNewsContext = JavaBeanGeneratorCreator.of(NewsContext.class).create();

    private final double configuredThreshold = 0.82;
    private final String sampleRawNews = "Apple stock surges after introducing groundbreaking AI ecosystem.";
    @Mock
    private Document mockDocument;


    @BeforeEach
    void setUp() {
        subject = new StockSemanticRepository(vectorStore,
                configuredThreshold,
                newsContextRepository);
    }

    @Test
    @DisplayName("Should build correct SearchRequest and return converted StockPrediction when similarity match is found")
    void shouldReturnPredictionWhenDocumentFound() {
        // Given
        Document matchedDocument = new Document("Sample Content", Map.of("key", "value"));
        StockPrediction expectedPrediction = new StockPrediction(); // Assumes default constructor

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(matchedDocument));

        when(this.newsContextRepository.findById(anyString())).thenReturn(Optional.of(mockNewsContext));

        // When
        Optional<StockPrediction> actual = subject.findStockPredictionByRawNews(sampleRawNews);


        assertThat(actual).isEqualTo(Optional.of(mockNewsContext.getStockPrediction()));

    }

    @Test
    @DisplayName("Should return Optional.empty() when vector store returns no matches above the threshold")
    void shouldReturnEmptyOptionalWhenNoDocumentsFound() {
        // Given
        // Vector store returns an empty list if nothing crosses the configured threshold
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(Collections.emptyList());

        // When
        Optional<StockPrediction> result = subject.findStockPredictionByRawNews(sampleRawNews);

        // Then
        assertFalse(result.isPresent(), "Result should be empty when vector store yields no results");

    }

    @Test
    @DisplayName("Should successfully convert and save NewsContext to VectorStore")
    void saveNewsContext_ShouldSaveSuccessfully() {

        // When
        subject.saveNewsContext(mockNewsContext);

        // Then
        // Verify that vectorStore.accept() was called exactly once
        verify(vectorStore).add(any());

        verify(newsContextRepository).save(any());

    }
}