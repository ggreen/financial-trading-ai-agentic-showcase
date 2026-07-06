package io.cloudNativeData.research.trader.agent.controller;

import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import io.cloudNativeData.trading.watch.StockPriceMovingAverageWatchList;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trades")
@RequiredArgsConstructor
public class ResearchTraderController {

    private final TradeRecommendationRepository repository;

    private final ResearchTraderService researchTraderService;

    @GetMapping("recommendations")
    public Iterable<TradeRecommendation> findAllTradeRecommendations() {
        return repository.findAll();
    }


    @GetMapping("stock/trader/watch")
    StockPriceMovingAverageWatchList getStockPriceMovingAverageWatchList(){
        return researchTraderService.getStockPriceMovingAverageWatchList();
    }

    @PostMapping("stock/price/moving/avg")
    public void saveStockPriceMovingAverage(@RequestBody StockPriceMovingAverage stockPriceMovingAverage) {
        researchTraderService.saveStockMovingAverage(stockPriceMovingAverage);
    }

}
