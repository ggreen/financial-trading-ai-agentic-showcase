package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.trading.TradeAdvice;
import io.cloudNativeData.trading.TradePrediction;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeAdviceServiceTest {

    private TradeAdviceService subject;
    private final StockNewsAnalysis news = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();
    @Mock
    private StockPricingExecution repository;
    @Mock
    private TradePredictionInference inference;
    private final TradePrediction prediction = JavaBeanGeneratorCreator.of(TradePrediction.class).create();

    @BeforeEach
    void setUp() {
        subject = new TradeAdviceService(inference, repository);
    }

    @Test
    void given_news_when_recommendation_then_advice_with_200_day_moving_avg() {

        var expected = TradeAdvice.builder().tradePrediction(prediction)
                .id(news.getId()).build();

        when(inference.recommend(any())).thenReturn(prediction);

        var actual = subject.recommend(news);

        verify(repository).calculateMovingAverage200(any());

        assertThat(actual).isEqualTo(expected);

    }
}