package io.cloudNativeData.sentiment.agent.functions.sink;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsDomainContextSinkTest {

    private static final NewsContext news =
            NewsContext.builder()
                    .rawNews("All good")
                    .stockPrediction(StockPrediction
                            .builder()
                            .newsSummary("All good summary")
                            .modelName("notSure")
                            .sentimentConfidence(BigDecimal.TEN)
                            .build()
                    )
                    .build();
    private NewsDomainContextSink subject;
    @Mock
    private Converter<NewsContext, List<Document>> converter;

    @Mock
    private VectorStore vectorStore;

    @BeforeEach
    void setUp() {
        subject = new NewsDomainContextSink(vectorStore,converter);
    }

    @Test
    void given_news_when_accept_then_save_content() {
        subject.accept(news);

        verify(vectorStore).add(any());
        verify(converter).convert(any());
    }
}