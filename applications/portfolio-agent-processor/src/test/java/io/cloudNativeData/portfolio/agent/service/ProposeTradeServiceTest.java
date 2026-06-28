package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.ai.RiskInference;
import io.cloudNativeData.portfolio.agent.repository.PortfolioTradeRepository;
import io.cloudNativeData.portfolio.agent.repository.QueryPortfolioRepository;
import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import io.cloudNativeData.trading.*;
import io.cloudNativeData.trading.risk.TradeRiskParameters;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposeTradeServiceTest {

    private ProposeTradeService subject;

    @Mock
    private QueryPortfolioRepository queryPortfolioRepository;

    @Mock
    private RiskInference riskInference;

    @Mock
    private PortfolioTradeRepository portfolioTradeRepository;

    @Mock
    private Converter<PortfolioTradeProposal, PortfolioTradeEntity> converter;


    private final PortfolioTradeProposal portfolioTradeProposal = JavaBeanGeneratorCreator.of(PortfolioTradeProposal.class).create();

    private TradeRecommendation tradeRecommendation;

    private final PortfolioTradeEntity  portfolioTradeEntity = JavaBeanGeneratorCreator.of(PortfolioTradeEntity.class)
            .create();

    private final BigDecimal maxAllocationPerTrade = BigDecimal.TWO;
    private final BigDecimal totalPortfolioValue = BigDecimal.valueOf(20000);
    private final BigDecimal stockPrice =BigDecimal.valueOf(100);
    private final BigDecimal newsConfidence = BigDecimal.valueOf(0.85);
    private final static Integer maxSellLimit = 500;
    private final static Integer baseSellQuantity = 50;

    @BeforeEach
    void setUp() {
        tradeRecommendation = JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();

        subject = new ProposeTradeService(queryPortfolioRepository,riskInference,portfolioTradeRepository, converter);
    }

    @Test
    void given_buy_trade_when_propose_then_return_trade() {

        when(queryPortfolioRepository.findMaxAllocationPerTrade()).thenReturn(maxAllocationPerTrade);
        when(queryPortfolioRepository.findTotalPortfolioValue()).thenReturn(totalPortfolioValue);
        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.BUY);
        tradeRecommendation.getStockNewsAnalysis().getStockPrediction()
                        .setSentimentConfidence(newsConfidence);

        tradeRecommendation.setPrice(stockPrice);

        int expectedQuantity = 340;

        var expected = PortfolioTradeProposal.builder()
                .id(tradeRecommendation.getId())
                .tradeRecommendation(tradeRecommendation)
                .quantity(expectedQuantity)
                .proposalStatus(ProposalStatus.Open)
                .build();

        var actual = subject.propose(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);

        verify(riskInference).predict(any(TradeRiskParameters.class));
        verify(portfolioTradeRepository).save(any());
    }

    @Test
    void given_sell_trade_when_propose_then_return_trade() {

        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.SELL);
        tradeRecommendation.getStockNewsAnalysis()
                .getStockPrediction()
                .setMarketSentiment(MarketSentiment.BEARISH);

        tradeRecommendation.getStockNewsAnalysis()
                .getStockPrediction()
                .setSentimentConfidence(newsConfidence);

        tradeRecommendation.setPrice(stockPrice);

        int expectedQuantity = 500;

        when(queryPortfolioRepository.findMaxSellLimit(anyString())).thenReturn(maxSellLimit);
        when(queryPortfolioRepository.findBaseSellQuantity(anyString())).thenReturn(baseSellQuantity);

        var expected = PortfolioTradeProposal.builder()
                .id(tradeRecommendation.getId())
                .tradeRecommendation(tradeRecommendation)
                .proposalStatus(ProposalStatus.Open)
                .quantity(expectedQuantity)
                .build();

        var actual = subject.propose(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);
        verify(riskInference).predict(any(TradeRiskParameters.class));
        verify(portfolioTradeRepository).save(any());
    }


    @Test
    void whenTradeActionIsNull() {

        TradePrediction invalidTradePrediction = TradePrediction.builder()
                .tradeConfidence(0)
                .adviceAction(null)
                .build();
        var tradeRecommendationWithNullAction = TradeRecommendation.builder()
                .id(tradeRecommendation.getId())
                .tradePrediction(invalidTradePrediction)
                .stockNewsAnalysis(tradeRecommendation.getStockNewsAnalysis())
                .build();

        var expected =  PortfolioTradeProposal
                .builder().id(tradeRecommendationWithNullAction.getId())
                .tradeRecommendation(tradeRecommendationWithNullAction)
                .riskPrediction(null)
                .proposalStatus(ProposalStatus.Invalid)
                .quantity(0).build();

        var actual = subject.propose(tradeRecommendationWithNullAction);

        verify(riskInference,never()).predict(any(TradeRiskParameters.class));

        verify(portfolioTradeRepository).save(any());
        assertThat(actual).isEqualTo(expected);
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

        var jsonMapper = new JsonMapper();

        var json = jsonMapper.writeValueAsString(tradeRecommendation);

        System.out.println(json);
    }

    @Test
    void findActiveTradeProposals() {
        Iterable<PortfolioTradeProposal> expected = List.of(this.portfolioTradeProposal);
        when(this.portfolioTradeRepository.findNonRejectTradeProposals()).thenReturn(expected);

        var actual = subject.findActiveTradeProposals();
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void acceptTradeProposalById() {

        when(portfolioTradeRepository.findById(anyString())).thenReturn(Optional.of(this.portfolioTradeEntity));

        subject.acceptTradeProposalById(tradeRecommendation.getId());

        verify(portfolioTradeRepository).save(any());
    }

    @Test
    void acceptTradeProposalById_WhenIdIsNull_ShouldReturnImmediately() {
        // Act
        subject.acceptTradeProposalById(null);

        // Assert
        verifyNoInteractions(portfolioTradeRepository);
    }

    @Test
    void acceptTradeProposalById_WhenProposalNotFound_ShouldLogErrorAndReturn() {
        // Arrange
        String tradeId = "invalid-id";
        when(portfolioTradeRepository.findById(tradeId)).thenReturn(Optional.empty());

        // Act
        subject.acceptTradeProposalById(tradeId);

        // Assert
        verify(portfolioTradeRepository, times(1)).findById(tradeId);
        verify(portfolioTradeRepository, never()).save(any());
    }

    @Test
    void acceptTradeProposalById_WhenTradeProposalIsNull_ShouldLogErrorAndReturn() {
        // Arrange
        String tradeId = "trade-123";
        PortfolioTradeEntity mockEntity = mock(PortfolioTradeEntity.class);

        when(portfolioTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getTradeProposal()).thenReturn(null);

        // Act
        subject.acceptTradeProposalById(tradeId);

        // Assert
        verify(portfolioTradeRepository, times(1)).findById(tradeId);
        verify(portfolioTradeRepository, never()).save(any());
    }

    @Test
    void acceptTradeProposalById_WhenValid_ShouldUpdateStatusAndSave() {
        // Arrange
        String tradeId = "trade-123";
        PortfolioTradeEntity mockEntity = mock(PortfolioTradeEntity.class);
        PortfolioTradeProposal mockProposal = mock(PortfolioTradeProposal.class);

        when(portfolioTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getTradeProposal()).thenReturn(mockProposal);

        // Act
        subject.acceptTradeProposalById(tradeId);

        // Assert
        verify(portfolioTradeRepository, times(1)).findById(tradeId);
        verify(mockProposal, times(1)).setProposalStatus(ProposalStatus.Accepted);
        verify(portfolioTradeRepository, times(1)).save(mockEntity);
    }



    @Test
    void rejectTradeProposalById() {

        when(portfolioTradeRepository.findById(anyString())).thenReturn(Optional.of(this.portfolioTradeEntity));

        subject.rejectTradeProposalById(tradeRecommendation.getId());

        verify(portfolioTradeRepository).save(any());
    }

    //***************

    @Test
    void given_buy_trade_with_zero_or_negative_price_when_propose_then_quantity_is_zero() {
        // Arrange
        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.BUY);
        tradeRecommendation.setPrice(BigDecimal.ZERO);
        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        // Act
        var actual = subject.propose(tradeRecommendation);

        // Assert
        assertThat(actual.getQuantity()).isZero();
        verify(riskInference).predict(any(TradeRiskParameters.class));
        verify(portfolioTradeRepository).save(any());
    }

    // ==========================================
    // ADDITIONAL TESTS FOR SELL QUANTITY
    // ==========================================

    @Test
    void given_sell_trade_with_null_prediction_data_when_propose_then_quantity_is_zero() {
        // Arrange
        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.SELL);
        tradeRecommendation.getStockNewsAnalysis().setStockPrediction(null);
        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        // Act
        var actual = subject.propose(tradeRecommendation);

        // Assert
        assertThat(actual.getQuantity()).isZero();
    }

    @Test
    void given_sell_trade_and_bullish_sentiment_when_propose_then_quantity_is_zero() {
        // Arrange
        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.SELL);
        tradeRecommendation.getStockNewsAnalysis().getStockPrediction()
                .setMarketSentiment(MarketSentiment.BULLISH);
        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        // Act
        var actual = subject.propose(tradeRecommendation);

        // Assert
        assertThat(actual.getQuantity()).isZero();
    }

    @Test
    void given_sell_trade_bearish_when_final_quantity_less_than_max_limit_then_return_calculated_quantity() {
        // Arrange
        when(converter.convert(any())).thenReturn(portfolioTradeEntity);

        tradeRecommendation.getTradePrediction().setAdviceAction(TradeAction.SELL);
        tradeRecommendation.getStockNewsAnalysis().getStockPrediction()
                .setMarketSentiment(MarketSentiment.BEARISH);
        tradeRecommendation.getTradePrediction().setTradeConfidence(0.5); // 50% confidence

        String ticker = tradeRecommendation.getStockNewsAnalysis().getTicker();
        when(queryPortfolioRepository.findBaseSellQuantity(ticker)).thenReturn(100);
        when(queryPortfolioRepository.findMaxSellLimit(ticker)).thenReturn(500);

        // expected final quantity: 100 * 0.5 = 50 (which is less than max limit 500)
        int expectedQuantity = 50;

        // Act
        var actual = subject.propose(tradeRecommendation);

        // Assert
        assertThat(actual.getQuantity()).isEqualTo(expectedQuantity);
    }

    // ==========================================
    // ADDITIONAL TESTS FOR REJECT PROPOSAL
    // ==========================================

    @Test
    void rejectTradeProposalById_WhenValid_ShouldUpdateStatusToRejectedAndSave() {
        // Arrange
        String tradeId = "trade-456";
        PortfolioTradeEntity mockEntity = mock(PortfolioTradeEntity.class);
        PortfolioTradeProposal mockProposal = mock(PortfolioTradeProposal.class);

        when(portfolioTradeRepository.findById(tradeId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.getTradeProposal()).thenReturn(mockProposal);

        // Act
        subject.rejectTradeProposalById(tradeId);

        // Assert
        verify(portfolioTradeRepository, times(1)).findById(tradeId);
        verify(mockProposal, times(1)).setProposalStatus(ProposalStatus.Rejected);
        verify(portfolioTradeRepository, times(1)).save(mockEntity);
    }


    @Test
    void determineBuy_when_price_isnull_return_0() {


        TradeRecommendation advice = TradeRecommendation.builder()
                .build();

        var actual = subject.determineBuyQuantity(advice);

        assertThat(actual).isEqualTo(0);
    }
}