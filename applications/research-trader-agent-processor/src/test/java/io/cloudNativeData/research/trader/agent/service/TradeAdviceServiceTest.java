package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.trading.*;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeAdviceServiceTest {

    private TradeAdviceService subject;

    @Mock
    private StockPriceService stockPriceService;

    @Mock
    private StockPricingExecution stockPricingExecution;

    @Mock
    private StockDailyPriceRepository stockDailyPriceRepository;

    @Mock
    private TradeRecommendationRepository tradeRecommendationRepository;

    @Mock
    private TradePredictionInference inference;

    @Mock
    private Converter<StockPriceDto, StockDailyPrice> dtoToPriceConverter;

    private final TradePrediction prediction = JavaBeanGeneratorCreator.of(TradePrediction.class).create();
    private final StockPriceDto dto = JavaBeanGeneratorCreator.of(StockPriceDto.class).create();
    private final StockDailyPrice stockDailyPrice = JavaBeanGeneratorCreator.of(StockDailyPrice.class).create();
    private final BigDecimal price = BigDecimal.TEN;
    private final BigDecimal movingAvg200 = BigDecimal.TEN;

    @BeforeEach
    void setUp() {
        subject = new TradeAdviceService(inference, stockPricingExecution, tradeRecommendationRepository,stockPriceService,stockDailyPriceRepository, dtoToPriceConverter);
    }

    @Test
    void given_news_when_recommendation_then_advice_with_200_day_moving_avg() {

        var news = StockNewsAnalysis.builder()
                .rawNews("Junit is awesome")
                .ticker("junit")
                .stockPrediction(StockPrediction.builder()
                        .marketSentiment(MarketSentiment.BULLISH)
                        .sentimentConfidence(BigDecimal.valueOf(99.99))
                        .build())
                .id("junit")
                .build();

        when(this.stockPriceService.getCurrentStockPrice(anyString())).thenReturn(dto);
        when(this.dtoToPriceConverter.convert(any())).thenReturn(this.stockDailyPrice);
        when(this.stockPricingExecution.calculateMovingAverage200(any())).thenReturn(movingAvg200);

        BigDecimal expectedPrice = BigDecimal.valueOf(10.2);
        var expected = TradeRecommendation.builder().tradePrediction(prediction)
                .id(news.getId())
                .stockNewsAnalysis(news)
                .price(expectedPrice)
                .build();


        when(inference.recommend(any())).thenReturn(prediction);

        var actual = subject.recommend(news);

        verify(stockPricingExecution).calculateMovingAverage200(any());
        verify(tradeRecommendationRepository).save(any(TradeRecommendation.class));
        verify(stockPriceService).getCurrentStockPrice(anyString());
        verify(stockDailyPriceRepository).save(any(StockDailyPrice.class));

        assertThat(actual).isEqualTo(expected);

    }
}