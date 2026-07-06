package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsContextToDocumentsConverterTest {

    // Assuming your class is named NewsContextConverter
    private NewsContextToDocumentsConverter subject;

    private final String configuredModelName = "semantic";

    @Mock
    private NewsContext newsContext;

    @Mock
    private StockPrediction stockPrediction;


    @BeforeEach
    void setUp() {
        // Injecting the modelName into the converter instance
        subject = new NewsContextToDocumentsConverter(configuredModelName);
    }

    @Test
    @DisplayName("Should successfully convert NewsContext into a Spring AI Document with correct metadata")
    void shouldConvertNewsContextToDocument() {
        // Given
        var rawNewsText = "Tech stocks are rallying following a stellar Q3 earnings report.";
        var summaryText = "Tech stocks rally on strong Q3 earnings.";
        var sentiment = MarketSentiment.BULLISH;
        var confidence = new BigDecimal("0.945");
        when(newsContext.getId()).thenReturn(rawNewsText);

        // When
        List<Document> result = subject.convert(newsContext);

        // Then
        assertNotNull(result, "The resulting list should not be null");
        assertEquals(1, result.size(), "The list should contain exactly one document");

        Document document = result.get(0);

        // Verify text content mapping
        assertEquals(rawNewsText, document.getText(), "The document content should match the raw news text");

        // Verify metadata mapping
        Map<String, Object> metadata = document.getMetadata();
        assertNotNull(metadata, "Metadata map should not be null");

    }

}