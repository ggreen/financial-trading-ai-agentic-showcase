package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.MarketSentiment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocumentToStockPredictionConverterTest {

    private DocumentToStockPredictionConverter converter;

    @BeforeEach
    void setUp() {
        converter = new DocumentToStockPredictionConverter();
    }

    @Test
    @DisplayName("Should successfully map all document metadata fields to StockPrediction")
    void shouldConvertDocumentWithFullMetadata() {
        // Given
        Map<String, Object> metadata = Map.of(
        "marketSentiment", "BULLISH",
        "sentimentConfidence", 0.925000011920929, // Mimicking an arbitrary Double from ,
        "newsSummary", "Strong quarterly revenue growth observed.",
        "modelName", "FinBERT-V2");

        Document sourceDocument = new Document("Raw text content", metadata);

        // When
        var result = converter.convert(sourceDocument);

        // Then
        assertNotNull(result);
        assertEquals(MarketSentiment.BULLISH, result.getMarketSentiment());
        assertEquals(BigDecimal.valueOf(0.925000011920929), result.getSentimentConfidence());
        assertEquals("Strong quarterly revenue growth observed.", result.getNewsSummary());
        assertEquals("FinBERT-V2", result.getModelName());
    }

    @Test
    @DisplayName("Should handle alternative Float data types gracefully for sentimentConfidence")
    void shouldConvertSafelyWhenConfidenceIsAFloat() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("marketSentiment", "BEARISH");
        metadata.put("sentimentConfidence", 0.75f); // Explicit Float type

        Document sourceDocument = new Document("Raw text content", metadata);

        // When
        var result = converter.convert(sourceDocument);

        // Then
        assertNotNull(result);
        // 0.75f evaluated as a double matches 0.75
        assertEquals(BigDecimal.valueOf(0.75), result.getSentimentConfidence());
    }

    @Test
    @DisplayName("Should return an empty StockPrediction when metadata fields are missing")
    void shouldHandleMissingOrNullMetadataGracefully() {
        // Given
        // Empty metadata map simulating a Document with no attributes populated
        Document sourceDocument = new Document("Raw text content", Map.of());

        // When
        var result = converter.convert(sourceDocument);

        // Then
        assertNotNull(result, "Converter must still yield a non-null object framework reference");
        assertNull(result.getMarketSentiment());
        assertNull(result.getSentimentConfidence());
        assertNull(result.getNewsSummary());
        assertNull(result.getModelName());
    }
}