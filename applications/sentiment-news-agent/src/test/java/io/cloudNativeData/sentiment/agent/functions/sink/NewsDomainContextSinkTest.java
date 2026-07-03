package io.cloudNativeData.sentiment.agent.functions.sink;

import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsDomainContextSinkTest {

    private static final NewsContext news =
            NewsContext.builder()
                    .id("All good")
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
    private StockNewsAnalyzerService stockNewsAnalyzerService;

    @BeforeEach
    void setUp() {
        subject = new NewsDomainContextSink(stockNewsAnalyzerService);
    }

    @Test
    void given_news_when_accept_then_save_content() {
        subject.accept(news);

        verify(stockNewsAnalyzerService).saveNewsContext(any(NewsContext.class));
    }
}