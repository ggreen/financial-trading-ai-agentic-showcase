package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.research.trader.agent.repository.StockDailyPriceRepository;
import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import io.cloudNativeData.trading.TradeParameters;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

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

        var summary200 = TradeParameters.builder()
                .prediction(stockNewsAnalysis.getStockPrediction())
                .movingAverage200(movingAverage200)
                .build();

        var tradePrediction = inference.recommend(summary200);

        log.info("predication: {}", tradePrediction);
        var tradeRecommendation = TradeRecommendation
                .builder()
                .id(stockNewsAnalysis.getId())
                .tradePrediction(tradePrediction)
                .stockNewsAnalysis(stockNewsAnalysis)
                .build();

        tradeRecommendationRepository.save(tradeRecommendation);
        return tradeRecommendation;
    }
}
