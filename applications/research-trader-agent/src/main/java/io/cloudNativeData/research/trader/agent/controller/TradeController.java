package io.cloudNativeData.research.trader.agent.controller;

import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.trading.TradeRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeRecommendationRepository repository;

    @GetMapping("recommendations")
    public Iterable<TradeRecommendation> findAllTradeRecommendations() {
        return repository.findAll();
    }
}
