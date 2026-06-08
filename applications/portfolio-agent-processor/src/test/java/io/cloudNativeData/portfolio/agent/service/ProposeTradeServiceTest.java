package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import io.cloudNativeData.trading.PortfolioTradeGeneration;
import io.cloudNativeData.trading.TradeAction;
import io.cloudNativeData.trading.TradeGeneration;
import io.cloudNativeData.trading.TradePrediction;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposeTradeServiceTest {

    private ProposeTradeService subject;

    @Mock
    private PortfolioRepository repository;
    private TradeGeneration tradeGeneration;
    private final BigDecimal maxAllocationPerTrade = BigDecimal.TWO;
    private final BigDecimal totalPortfolioValue = BigDecimal.valueOf(20000);
    private final BigDecimal stockPrice =BigDecimal.valueOf(100);
    private final BigDecimal newsConfidence = BigDecimal.valueOf(0.85);

    @BeforeEach
    void setUp() {
        tradeGeneration = JavaBeanGeneratorCreator.of(TradeGeneration.class).create();

        subject = new ProposeTradeService(repository);
    }

    @Test
    void given_trader_when_propose_then_return_trade() {

        when(repository.findMaxAllocationPerTrade()).thenReturn(maxAllocationPerTrade);
        when(repository.findTotalPortfolioValue()).thenReturn(totalPortfolioValue);
        tradeGeneration.getTradePrediction().setAdviceAction(TradeAction.BUY);
        tradeGeneration.getStockNewsGeneration().getPrediction()
                        .setConfidence(newsConfidence);

        tradeGeneration.getTradePrediction().setPrice(stockPrice);

        int expectedQuantity = 340;

        var expected = PortfolioTradeGeneration.builder()
                .id(tradeGeneration.getId())
                .tradeGeneration(tradeGeneration)
                .quantity(expectedQuantity)
                .build();

        var actual = subject.propose(tradeGeneration);

        assertThat(actual).isEqualTo(expected);
    }
}