package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.service.stocks.StockPriceService;
import io.cloudNativeData.trading.*;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.integration.Publisher;
import org.apache.catalina.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchTraderServiceTest {

    private ResearchTraderService subject;

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
    @Mock
    private  Publisher<TradeRecommendation> tradeRecommendationPublisher ;


    private final StockPriceDto dto = JavaBeanGeneratorCreator.of(StockPriceDto.class).create();
    private final StockDailyPrice stockDailyPrice = JavaBeanGeneratorCreator.of(StockDailyPrice.class).create();
    private final BigDecimal price = BigDecimal.TEN;
    private final BigDecimal movingAvg200 = BigDecimal.TEN;
    private final StockPriceMovingAverage stockPriceMovingAverage= JavaBeanGeneratorCreator.of(StockPriceMovingAverage.class).create();
    private final TradeRecommendation tradeRecommendation =  JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();


    @BeforeEach
    void setUp() {
        subject = new ResearchTraderService(inference, stockPricingExecution,
                tradeRecommendationRepository,stockPriceService,stockDailyPriceRepository,
                dtoToPriceConverter,
                tradeRecommendationPublisher);
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
        TradePrediction prediction = TradePrediction.builder()
                .adviceAction(TradeAction.BUY)
                .tradeConfidence(0.99)
                .build();
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


    @Test
    void given_nullStockPrice_when_recommend_then_doNotSaveDailyPrice() {
        var news = createNewsAnalysis(MarketSentiment.NEUTRAL, 0.5);

        TradePrediction prediction = TradePrediction.builder()
                .adviceAction(TradeAction.BUY)
                .tradeConfidence(0.99)
                .build();
        when(this.stockPriceService.getCurrentStockPrice(anyString())).thenReturn(null);
        when(this.stockPricingExecution.calculateMovingAverage200(any())).thenReturn(BigDecimal.valueOf(100.0));
        when(inference.recommend(any())).thenReturn(prediction);

        var actual = subject.recommend(news);
        assertThat(actual).isNotNull();

        verify(stockDailyPriceRepository, never()).save(any(StockDailyPrice.class));
        verifyNoInteractions(dtoToPriceConverter);
        assertThat(actual.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void given_nullMovingAverage_when_recommend_then_returnPriceZero() {
        var news = createNewsAnalysis(MarketSentiment.BULLISH, 0.5);

        TradePrediction prediction
                 = TradePrediction.builder()
                .adviceAction(TradeAction.BUY)
                .tradeConfidence(0.99)
                .build();
        when(this.stockPriceService.getCurrentStockPrice(anyString())).thenReturn(dto);
        when(this.stockPricingExecution.calculateMovingAverage200(any())).thenReturn(null);
        when(inference.recommend(any())).thenReturn(prediction);

        var actual = subject.recommend(news);

        assertThat(actual).isNotNull();

        assertThat(actual.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }


    private StockNewsAnalysis createNewsAnalysis(MarketSentiment sentiment, double confidence) {
        return StockNewsAnalysis.builder()
                .rawNews("Coverage Booster")
                .ticker("TEST")
                .stockPrediction(StockPrediction.builder()
                        .marketSentiment(sentiment)
                        .sentimentConfidence(BigDecimal.valueOf(confidence))
                        .build())
                .id("test-id")
                .build();
    }

    @Test
    void given_neutralMarketSentiment_when_recommend_then_returnNull() {
        // Arrange
        String tickerId = "neutral-ticker";
        var news = StockNewsAnalysis.builder()
                .rawNews("Market is flat today.")
                .ticker(tickerId)
                .stockPrediction(StockPrediction.builder()
                        .marketSentiment(MarketSentiment.BULLISH)
                        .sentimentConfidence(BigDecimal.valueOf(0.85)) // Confidence shouldn't affect NEUTRAL
                        .build())
                .id(tickerId)
                .build();

        TradePrediction neutralPrediction = TradePrediction.builder()
                .adviceAction(TradeAction.NA)
                .tradeConfidence(1)
                .build();
        BigDecimal movingAvg200 = BigDecimal.valueOf(150.00);

        when(this.stockPriceService.getCurrentStockPrice(tickerId)).thenReturn(dto);
        when(this.dtoToPriceConverter.convert(dto)).thenReturn(this.stockDailyPrice);
        when(this.stockPricingExecution.calculateMovingAverage200(any())).thenReturn(movingAvg200);
        when(inference.recommend(any())).thenReturn(neutralPrediction);

        // Act
        var actual = subject.recommend(news);

        // Assert
        // For NEUTRAL sentiment, adjustment is 0.0. Price should match movingAvg200 exactly (150.00)
        assertThat(actual).isNull();
    }


    @Test
    void testRecommendSell_ShouldFindUpdateSaveAndPublish() {



        // Stub repository query to simulate returning this entity match
        when(tradeRecommendationRepository.findById(anyString())).thenReturn(Optional.of(this.tradeRecommendation));
        when(tradeRecommendationRepository.save(any(TradeRecommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: The CQ listener triggers the target workflow
        subject.recommendSell(this.stockPriceMovingAverage);


        verify(tradeRecommendationRepository, times(1)).findById(anyString());
        verify(tradeRecommendationRepository, times(1)).save(any(TradeRecommendation.class));
        verify(tradeRecommendationPublisher).send(any());


    }

    @Test
    void testRecommendSell_WhenRecordNotFound_ShouldGracefullyAbort() {
        // Given
        when(tradeRecommendationRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        subject.recommendSell(this.stockPriceMovingAverage);

        // Then: Ensure mutations and persistence invocations are skipped completely
        verify(tradeRecommendationRepository, times(1)).findById(anyString());
        verify(tradeRecommendationRepository, never()).save(any(TradeRecommendation.class));
        verify(tradeRecommendationPublisher, never()).send(any());
    }

}