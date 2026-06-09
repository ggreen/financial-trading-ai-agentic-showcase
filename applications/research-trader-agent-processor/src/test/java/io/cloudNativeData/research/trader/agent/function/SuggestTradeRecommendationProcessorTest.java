package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.service.TradeAdviceService;
import io.cloudNativeData.trading.news.StockNewsGeneration;
import io.cloudNativeData.trading.TradeRecommendation;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestTradeRecommendationProcessorTest {

    private SuggestTradeAdviceProcessor subject;
    private final StockNewsGeneration news = JavaBeanGeneratorCreator.of(StockNewsGeneration.class).create();

    @Mock
    private TradeAdviceService service;

    @BeforeEach
    void setUp() {
        subject = new SuggestTradeAdviceProcessor(service);
    }

    @Test
    void apply() {
        var expected = TradeRecommendation.builder().build();
        when(service.recommend(any(StockNewsGeneration.class))).thenReturn(expected);

        var actual = subject.apply(news);

        assertThat(actual).isEqualTo(expected);
    }
}