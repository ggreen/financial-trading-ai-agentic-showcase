package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.StockPrediction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.convert.converter.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockNewsAnalysisSemanticRepositoryTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private Converter<Document, StockPrediction> converter;

    private StockNewsAnalysisSemanticRepository subject;

    private final double configuredThreshold = 0.82;
    private final String sampleRawNews = "Apple stock surges after introducing groundbreaking AI ecosystem.";

    @BeforeEach
    void setUp() {
        subject = new StockNewsAnalysisSemanticRepository(vectorStore, configuredThreshold, converter);
    }

    @Test
    @DisplayName("Should build correct SearchRequest and return converted StockPrediction when similarity match is found")
    void shouldReturnPredictionWhenDocumentFound() {
        // Given
        Document matchedDocument = new Document("Sample Content", Map.of("key", "value"));
        StockPrediction expectedPrediction = new StockPrediction(); // Assumes default constructor

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(matchedDocument));
        when(converter.convert(matchedDocument))
                .thenReturn(expectedPrediction);

        // When
        Optional<StockPrediction> result = subject.findStockPredictionByRawNews(sampleRawNews);

        // Then
        assertTrue(result.isPresent(), "Result should contain a StockPrediction");
        assertEquals(expectedPrediction, result.get());

        // Verify SearchRequest parameters via ArgumentCaptor
        ArgumentCaptor<SearchRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore, times(1)).similaritySearch(searchRequestCaptor.capture());

        SearchRequest capturedRequest = searchRequestCaptor.getValue();
        assertEquals(sampleRawNews, capturedRequest.getQuery(), "Query should match incoming raw text");
        assertEquals(1, capturedRequest.getTopK(), "TopK should strictly be 1");
        assertEquals(configuredThreshold, capturedRequest.getSimilarityThreshold(), "Should use injected similarity threshold");

        // Verify conversion logic was triggered for the matching document
        verify(converter, times(1)).convert(matchedDocument);
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

        // Verify converter was never interacted with since no document exists
        verifyNoInteractions(converter);
    }
}