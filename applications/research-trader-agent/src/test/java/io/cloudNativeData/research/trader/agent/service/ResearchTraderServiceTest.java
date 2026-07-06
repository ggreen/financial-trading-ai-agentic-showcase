package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.properties.StockListenerConfig;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPriceMovingAverageRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.service.stocks.StockPriceService;
import io.cloudNativeData.trading.*;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import io.cloudNativeData.trading.watch.StockPriceMovingAverageWatchList;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.integration.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchTraderServiceTest {

    private ResearchTraderService subject;

    @Mock
    private StockPriceService stockPriceService;

    @Mock
    private StockPriceMovingAverageRepository stockPriceMovingAverageRepository;

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


    @Mock
    private  StockListenerConfig stockListenerConfig;

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
                tradeRecommendationPublisher,
                stockPriceMovingAverageRepository,
                stockListenerConfig);
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
        when(this.dtoToPriceConverter.convert(any())).thenReturn(this.stockDailyPrice);

        // When: The CQ listener triggers the target workflow
        subject.recommendSell(this.stockPriceMovingAverage);


        verify(tradeRecommendationRepository, times(1)).findById(anyString());
        verify(tradeRecommendationRepository, times(1)).save(any(TradeRecommendation.class));
        verify(tradeRecommendationPublisher).send(any());
        verify(this.stockDailyPriceRepository).save(any(StockDailyPrice.class));

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
        verify(this.stockDailyPriceRepository,never()).save(any(StockDailyPrice.class));

    }


    @Test
    void testRecommendBuy_ShouldFindUpdateSaveAndPublish() {



        // Stub repository query to simulate returning this entity match
        when(tradeRecommendationRepository.findById(anyString())).thenReturn(Optional.of(this.tradeRecommendation));
        when(tradeRecommendationRepository.save(any(TradeRecommendation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.dtoToPriceConverter.convert(any())).thenReturn(this.stockDailyPrice);

        // When: The CQ listener triggers the target workflow
        subject.recommendBuy(this.stockPriceMovingAverage);


        verify(tradeRecommendationRepository, times(1)).findById(anyString());
        verify(tradeRecommendationRepository, times(1)).save(any(TradeRecommendation.class));
        verify(tradeRecommendationPublisher).send(any());
        verify(this.stockDailyPriceRepository).save(any(StockDailyPrice.class));
        verify(this.stockPriceMovingAverageRepository).deleteById(anyString());

    }

    @Test
    @DisplayName("Should add to stock price moving average when no TradeAction")
    void recommend_WhenTradeActionIsNull_ShouldReturnNull() {
        // Arrange
        String id = "analysis-123";
        String ticker = "AAPL";

        StockNewsAnalysis mockNewsAnalysis = mock(StockNewsAnalysis.class);
        StockPrediction mockPrediction = mock(StockPrediction.class);

        when(mockNewsAnalysis.getId()).thenReturn(id);
        when(mockNewsAnalysis.getTicker()).thenReturn(ticker);
        when(mockNewsAnalysis.getStockPrediction()).thenReturn(mockPrediction);
        when(mockPrediction.getMarketSentiment()).thenReturn(MarketSentiment.NEUTRAL);
        when(mockPrediction.getSentimentConfidence()).thenReturn(BigDecimal.valueOf(0.5));

        // Mocking stock price steps
        when(stockPriceService.getCurrentStockPrice(ticker)).thenReturn(new StockPriceDto());
        when(dtoToPriceConverter.convert(any(StockPriceDto.class))).thenReturn(new StockDailyPrice());
        when(this.stockPricingExecution.calculateMovingAverage200(id)).thenReturn(BigDecimal.valueOf(150.00));

        // Forcing TradeAction to be NA or TradePrediction to return an empty/null action
        TradePrediction mockTradePrediction = mock(TradePrediction.class);
        when(mockTradePrediction.getAdviceAction()).thenReturn(TradeAction.NA);
        when(inference.recommend(any(TradeParameters.class))).thenReturn(mockTradePrediction);

        // Act
        TradeRecommendation result = subject.recommend(mockNewsAnalysis);

        // Assert
        verify(stockPriceMovingAverageRepository).save(any(StockPriceMovingAverage.class));
    }

    @Test
    void shouldReturnTradeWatchCriteriaWithExpectedData() {
        // Given
        String expectedBuyQuery = "price < ma50";
        String expectedSellQuery = "price > ma200";

        Iterable<StockPriceMovingAverage> mockRecords = List.of(this.stockPriceMovingAverage);
        when(stockPriceMovingAverageRepository.findAll()).thenReturn(mockRecords);
        when(stockListenerConfig.getBuyerQuery()).thenReturn(expectedBuyQuery);
        when(stockListenerConfig.getSellerQuery()).thenReturn(expectedSellQuery);

        // When
        StockPriceMovingAverageWatchList result = subject.getStockPriceMovingAverageWatchList();

        // Then
        assertNotNull(result, "The returned criteria should not be null");
        assertEquals(mockRecords, result.stockPriceMovingAverages(), "Tickers should match the IDs from the repository");
        assertEquals(expectedBuyQuery, result.buyQuery(), "Buy query should match the configuration");
        assertEquals(expectedSellQuery, result.sellQuery(), "Sell query should match the configuration");

        // Verify interactions (Optional but good practice)
        verify(stockPriceMovingAverageRepository).findAllIds();
        verify(stockListenerConfig).getBuyerQuery();
        verify(stockListenerConfig).getSellerQuery();
    }

    @Test
    void testSaveStockMovingAverage_Success() {
        // Act: Call the method under test
        subject.saveStockMovingAverage(this.stockPriceMovingAverage);

        // Assert: Verify that the repository's save method was called exactly once with the correct object
        verify(stockPriceMovingAverageRepository, times(1)).save(any(StockPriceMovingAverage.class));
    }

    @Test
    void recommend_WhenTradeActionIsNA_ShouldSaveStockPriceMovingAverageAndReturnNull() {
        // 1. Arrange (Setup Mock Data)
        String ticker = "AAPL";
        String analysisId = "1L";

        StockNewsAnalysis stockNewsAnalysis = mock(StockNewsAnalysis.class);
        StockPrediction stockPrediction = mock(StockPrediction.class);
//        s stockPrice = mock(StockPrice.class);
        StockPriceDto stockPrice = mock(StockPriceDto.class);
        TradePrediction tradePrediction = mock(TradePrediction.class);

        BigDecimal movingAverage200 = new BigDecimal("150.00");

        when(stockNewsAnalysis.getTicker()).thenReturn(ticker);
        when(this.dtoToPriceConverter.convert(any())).thenReturn(this.stockDailyPrice);
        when(this.stockPriceService.getCurrentStockPrice(anyString())).thenReturn(stockPrice);
        when(stockNewsAnalysis.getId()).thenReturn(analysisId);
        when(stockNewsAnalysis.getStockPrediction()).thenReturn(stockPrediction);

        // Setup prediction details for the calculation
        when(stockPrediction.getMarketSentiment()).thenReturn(MarketSentiment.BULLISH); // Adjust as needed
        when(stockPrediction.getSentimentConfidence()).thenReturn(new BigDecimal("0.85"));

        when(stockPriceService.getCurrentStockPrice(ticker)).thenReturn(stockPrice);
        when(this.stockPricingExecution.calculateMovingAverage200(analysisId)).thenReturn(movingAverage200);

        // Crucial Part: Force the inference to return NA action to enter the target IF block
        when(inference.recommend(any(TradeParameters.class))).thenReturn(tradePrediction);
        when(tradePrediction.getAdviceAction()).thenReturn(TradeAction.NA);

        // 2. Act
        TradeRecommendation result = subject.recommend(stockNewsAnalysis);

        // 3. Assert
        assertNull(result, "Should return null when trade action is NA");

        // Verify that the moving average repository saved the entity
        verify(stockPriceMovingAverageRepository, times(1)).save(any(StockPriceMovingAverage.class));

        // Verify standard flows still happened
        verify(stockDailyPriceRepository, times(1)).save(any(StockDailyPrice.class));
        verify(tradeRecommendationRepository, times(1)).save(any(TradeRecommendation.class));
    }
}