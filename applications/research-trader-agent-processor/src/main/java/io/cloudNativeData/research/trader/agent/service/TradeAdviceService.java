package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeParameters;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeAdviceService {

    private final TradePredictionInference inference;
    private final StockPricingExecution repository;
    private final TradeRecommendationRepository tradeRecommendationRepository;
    private final StockPriceService stockPriceService;
    private final StockDailyPriceRepository stockDailyPriceRepository;
    private final Converter<StockPriceDto, StockDailyPrice> dtoToPriceConverter;


    public TradeRecommendation recommend(StockNewsAnalysis stockNewsAnalysis)
    {

        var stockPrice = stockPriceService.getCurrentStockPrice(stockNewsAnalysis.getTicker());

        if(stockPrice != null)
            stockDailyPriceRepository.save(dtoToPriceConverter.convert(stockPrice));

        var movingAverage200 = repository
                .calculateMovingAverage200(new String[]{stockNewsAnalysis.getId()});


        log.info("MovingAverage 200 stock price {}", movingAverage200);


        var summary200 = TradeParameters.builder()
                .prediction(stockNewsAnalysis.getStockPrediction())
                .movingAverage200(movingAverage200)
                .build();

        var tradePrediction = inference.recommend(summary200);
        log.info("predication: {}", tradePrediction);

        var price = recommendStockPrice(movingAverage200,stockNewsAnalysis.getStockPrediction().getMarketSentiment(),
                stockNewsAnalysis.getStockPrediction().getSentimentConfidence().doubleValue());


        log.info("price]: {}", price);

        var tradeRecommendation = TradeRecommendation
                .builder()
                .id(stockNewsAnalysis.getId())
                .tradePrediction(tradePrediction)
                .price(price)
                .stockNewsAnalysis(stockNewsAnalysis)
                .build();

        tradeRecommendationRepository.save(tradeRecommendation);
        return tradeRecommendation;
    }

    private BigDecimal recommendStockPrice(BigDecimal movingAverage200Day, MarketSentiment marketSentiment, double confidence) {

        if(movingAverage200Day == null)
            return BigDecimal.ZERO;

        // Sanity check for confidence bounds
        if (confidence < 0.0) confidence = 0.0;
        if (confidence > 1.0) confidence = 1.0;

        double adjustmentPercent = switch (marketSentiment) {
            case MarketSentiment.BULLISH -> {
                // Highly bullish = willing to pay a slight premium so we don't miss the boat.
                // Low confidence bullish = want to buy at a deeper discount below the MA.
                // Range: -5% (low confidence) to +2% (high confidence)
                double minPremium = -0.05;
                double maxPremium = 0.02;

                yield minPremium + (confidence * (maxPremium - minPremium));
            }
            case MarketSentiment.BEARISH -> {
                // Bearish market means the price will likely drop below the 200-day MA.
                // High bearish confidence = expect a major drop, set order lower to catch the bottom.
                // Low bearish confidence = minor drop, set order closer to the MA line.
                // Range: -2% (low confidence bearish) to -10% (high confidence bearish)
                double maxDiscount = -0.10;
                double minDiscount = -0.02;

                // High confidence maps to the maximum discount
                yield minDiscount - (confidence * (minDiscount - maxDiscount));
            }
            case NEUTRAL ->  0.0;
        };

        // Calculate final recommended price
        double recommendedPrice = movingAverage200Day.doubleValue() * (1.0 + adjustmentPercent);

        // Round to 2 decimal places for currency format
        return BigDecimal.valueOf(Math.round(recommendedPrice * 100.0) / 100.0);
    }
}
