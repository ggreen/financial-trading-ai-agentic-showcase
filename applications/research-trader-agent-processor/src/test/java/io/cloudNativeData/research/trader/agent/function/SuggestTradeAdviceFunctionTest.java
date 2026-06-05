package io.cloudNativeData.research.trader.agent.function;

import io.cloudNativeData.research.trader.agent.service.TradeAdviceService;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeAdvice;
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
class SuggestTradeAdviceFunctionTest {

    private SuggestTradeAdviceFunction subject;
    private final StockNewsAnalysis news = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();

    @Mock
    private TradeAdviceService service;

    @BeforeEach
    void setUp() {
        subject = new SuggestTradeAdviceFunction(service);
    }

    @Test
    void apply() {
        var expected = TradeAdvice.builder().build();
        when(service.recommend(any(StockNewsAnalysis.class))).thenReturn(expected);

        var actual = subject.apply(news);

        assertThat(actual).isEqualTo(expected);
    }
}