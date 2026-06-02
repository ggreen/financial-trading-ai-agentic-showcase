package io.cloudNativeData.sentiment.agent.functions;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.StockNewsAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockNewAnalyzerProcessorTest {

    private static final String rawNews = "";

    @Mock
    private StockAnalysisInference inference;

    private StockNewAnalyzerProcessor subject;

    @BeforeEach
    void setUp() {
        subject = new StockNewAnalyzerProcessor(inference);
    }

    @Test
    void apply() {
        var expected = StockNewsAnalysis.builder().build();
        when(inference.infer(rawNews)).thenReturn(expected);

        var actual = subject.apply(rawNews);
        assertThat(actual).isEqualTo(expected);
    }
}