package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeAction;
import io.cloudNativeData.trading.TradeRecommendation;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposeTradeServiceTest {

    private ProposeTradeService subject;

    @Mock
    private PortfolioRepository repository;
    private TradeRecommendation tradeRecommendation;
    private final BigDecimal maxAllocationPerTrade = BigDecimal.TWO;
    private final BigDecimal totalPortfolioValue = BigDecimal.valueOf(20000);
    private final BigDecimal stockPrice =BigDecimal.valueOf(100);
    private final BigDecimal newsConfidence = BigDecimal.valueOf(0.85);

    @BeforeEach
    void setUp() {
        tradeRecommendation = JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();

        subject = new ProposeTradeService(repository);
    }

    @Test
    void given_trader_when_propose_then_return_trade() {

        when(repository.findMaxAllocationPerTrade()).thenReturn(maxAllocationPerTrade);
        when(repository.findTotalPortfolioValue()).thenReturn(totalPortfolioValue);
        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.BUY);
        tradeRecommendation.getStockNewsGeneration().getPrediction()
                        .setConfidence(newsConfidence);

        tradeRecommendation.getTradePrediction().setPrice(stockPrice);

        int expectedQuantity = 340;

        var expected = PortfolioTradeProposal.builder()
                .id(tradeRecommendation.getId())
                .tradeRecommendation(tradeRecommendation)
                .quantity(expectedQuantity)
                .build();

        var actual = subject.propose(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);
    }
}