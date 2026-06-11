package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.ai.RiskInference;
import io.cloudNativeData.portfolio.agent.repository.PortfolioRepository;
import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeAction;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.risk.TradeRiskParameters;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposeTradeServiceTest {

    private ProposeTradeService subject;

    @Mock
    private PortfolioRepository repository;

    @Mock
    private RiskInference riskInference;

    private TradeRecommendation tradeRecommendation;
    private final BigDecimal maxAllocationPerTrade = BigDecimal.TWO;
    private final BigDecimal totalPortfolioValue = BigDecimal.valueOf(20000);
    private final BigDecimal stockPrice =BigDecimal.valueOf(100);
    private final BigDecimal newsConfidence = BigDecimal.valueOf(0.85);
    private final static Integer maxSellLimit = 500;
    private final static Integer baseSellQuantity = 50;

    @BeforeEach
    void setUp() {
        tradeRecommendation = JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();

        subject = new ProposeTradeService(repository,riskInference);
    }

    @Test
    void given_buy_trade_when_propose_then_return_trade() {

        when(repository.findMaxAllocationPerTrade()).thenReturn(maxAllocationPerTrade);
        when(repository.findTotalPortfolioValue()).thenReturn(totalPortfolioValue);

        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.BUY);
        tradeRecommendation.getStockNewsAnalysis().getStockPrediction()
                        .setSentimentConfidence(newsConfidence);

        tradeRecommendation.getTradePrediction().setPrice(stockPrice);

        int expectedQuantity = 340;

        var expected = PortfolioTradeProposal.builder()
                .id(tradeRecommendation.getId())
                .tradeRecommendation(tradeRecommendation)
                .quantity(expectedQuantity)
                .build();

        var actual = subject.propose(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);

        verify(riskInference).predict(any(TradeRiskParameters.class));
    }

    @Test
    void given_sell_trade_when_propose_then_return_trade() {

        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.SELL);
        tradeRecommendation.getStockNewsAnalysis()
                .getStockPrediction()
                .setMarketSentiment(MarketSentiment.BEARISH);

        tradeRecommendation.getStockNewsAnalysis()
                .getStockPrediction()
                .setSentimentConfidence(newsConfidence);

        tradeRecommendation.getTradePrediction().setPrice(stockPrice);

        int expectedQuantity = 500;

        when(repository.findMaxSellLimit(anyString())).thenReturn(maxSellLimit);
        when(repository.findBaseSellQuantity(anyString())).thenReturn(baseSellQuantity);

        var expected = PortfolioTradeProposal.builder()
                .id(tradeRecommendation.getId())
                .tradeRecommendation(tradeRecommendation)
                .quantity(expectedQuantity)
                .build();

        var actual = subject.propose(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);
        verify(riskInference).predict(any(TradeRiskParameters.class));
    }


    /*
    How would you determine a trade recommendation risk based on the following JSON



{"id":"33","stockNewsAnalysis":
{"id":"33","stockPrediction":{"confidence":
0.33,"marketSentiment":"BULLISH"},
"newsSummary":"Good",
"ticker":"APPL"},
"tradePrediction":{"adviceAction":"SELL",
"price":0.2323,
"tradeConfidence":8.232}}
     */
    @Test
    void json() {

        JsonMapper jsonMapper = new JsonMapper();

        var json = jsonMapper.writeValueAsString(tradeRecommendation);

        System.out.println(json);
    }
}