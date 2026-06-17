package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.TradePrediction;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

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

    @BeforeEach
    void setUp() {
        subject = new TradeAdviceService(inference, stockPricingExecution, tradeRecommendationRepository,stockPriceService,stockDailyPriceRepository, dtoToPriceConverter);
    }

    @Test
    void given_news_when_recommendation_then_advice_with_200_day_moving_avg() {

        when(this.stockPriceService.getCurrentStockPrice(anyString())).thenReturn(dto);
        when(this.dtoToPriceConverter.convert(any())).thenReturn(this.stockDailyPrice);
        var expected = TradeRecommendation.builder().tradePrediction(prediction)
                .id(news.getId())
                .stockNewsAnalysis(news)
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