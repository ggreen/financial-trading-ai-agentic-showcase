package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.trading.TradePrediction;
import io.cloudNativeData.trading.TradeRecommendation;
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
    private StockPricingExecution stockPricingExecution;

    @Mock
    private TradeRecommendationRepository tradeRecommendationRepository;

    @Mock
    private TradePredictionInference inference;
    private final TradePrediction prediction = JavaBeanGeneratorCreator.of(TradePrediction.class).create();

    @BeforeEach
    void setUp() {
        subject = new TradeAdviceService(inference, stockPricingExecution, tradeRecommendationRepository);
    }

    @Test
    void given_news_when_recommendation_then_advice_with_200_day_moving_avg() {

        var expected = TradeRecommendation.builder().tradePrediction(prediction)
                .id(news.getId())
                .stockNewsAnalysis(news)
                .build();


        when(inference.recommend(any())).thenReturn(prediction);

        var actual = subject.recommend(news);

        verify(stockPricingExecution).calculateMovingAverage200(any());
        verify(tradeRecommendationRepository).save(any(TradeRecommendation.class));

        assertThat(actual).isEqualTo(expected);

    }
}