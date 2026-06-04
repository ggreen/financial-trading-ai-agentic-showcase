package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradeSuggestionInference;
import io.cloudNativeData.research.trader.agent.repository.StockRepository;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.StockTradeAdvice;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeAdviceServiceTest {

    private TradeAdviceService subject;
    private final StockNewsAnalysis news = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();
    @Mock
    private StockRepository repository;
    @Mock
    private TradeSuggestionInference inference;

    @BeforeEach
    void setUp() {
        subject = new TradeAdviceService(inference, repository);
    }

    @Test
    void given_news_when_recommendation_then_advice_with_200_day_moving_avg() {

        var expected = JavaBeanGeneratorCreator.of(StockTradeAdvice.class).create();


        when(inference.recommend(any())).thenReturn(expected);

        var actual = subject.recommend(news);

        verify(repository).calculateMovingAverage200(anyString());

        assertThat(actual).isEqualTo(expected);


    }
}