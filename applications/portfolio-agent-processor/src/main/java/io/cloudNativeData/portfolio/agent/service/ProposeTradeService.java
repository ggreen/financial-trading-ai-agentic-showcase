package io.cloudNativeData.portfolio.agent.service;

import io.cloudNativeData.portfolio.agent.ai.RiskInference;
import io.cloudNativeData.portfolio.agent.repository.PortfolioTradeRepository;
import io.cloudNativeData.portfolio.agent.repository.QueryPortfolioRepository;
import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.ProposalStatus;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.risk.TradeRiskParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposeTradeService {
    private final QueryPortfolioRepository repository;
    private final RiskInference riskInference;
    private final PortfolioTradeRepository tradeRepository;
    private final Converter<PortfolioTradeProposal, PortfolioTradeEntity> portfolioTradeProposalToEntity;


    /**
     *
     * @param trade trade recommendation
     * @return the trader proposal
     */
    public PortfolioTradeProposal propose(TradeRecommendation trade) {

        var tradeAction = trade.getTradePrediction().getAdviceAction();

        log.info("Proposing trade action: {}", tradeAction);

        PortfolioTradeProposal proposal;
        if (tradeAction == null) {
            log.error("Proposing trade action is null, will not process, because a proposal is invalid");
            proposal = PortfolioTradeProposal.builder()
                    .id(trade.getId())
                    .tradeRecommendation(trade)
                    .proposalStatus(ProposalStatus.Invalid)
                    .build();

        } else {

            var quantity =
                    switch (tradeAction) {
                        case BUY -> determineBuyQuantity(trade);
                        case SELL -> determineSellQuantity(trade);
                        default -> 0;
                    };

            //Calculate risk
            var stockNewsAnalysis = trade.getStockNewsAnalysis();
            var stockPrediction = (stockNewsAnalysis != null) ? stockNewsAnalysis.getStockPrediction() : null;
            var tradePrediction = trade.getTradePrediction();

            var riskPrediction = riskInference.predict(
                    TradeRiskParameters.builder()
                            .stockPrediction(stockPrediction)
                            .tradeAction(tradePrediction != null ? tradePrediction.getAdviceAction() : null)
                            .newsSummary(stockPrediction != null ? stockPrediction.getNewsSummary() : null)
                            .ticker(stockNewsAnalysis != null ? stockNewsAnalysis.getTicker() : null)
                            .quantity(quantity)
                            .build());


            proposal = PortfolioTradeProposal.builder()
                    .quantity(quantity)
                    .tradeRecommendation(trade)
                    .id(trade.getId())
                    .riskPrediction(riskPrediction)
                    .proposalStatus(ProposalStatus.Open)
                    .build();
        }


        return saveProposal(proposal);

    }

    /**
     *
     * @param proposal the trade postal
     * @return the saved proposal
     */
    private PortfolioTradeProposal saveProposal(PortfolioTradeProposal proposal) {

        var entity = portfolioTradeProposalToEntity.convert(proposal);

        this.tradeRepository.save(entity);

        return proposal;
    }

    /*
     Implement determine stock trade sell quantity
        private String id;
    private TradePrediction tradePrediction;
    private StockNewsAnalysis stockNewsAnalysis;
    private BigDecimal price;
        private StockPrediction prediction_bull_or_bearish ;
    private String newsSummary;
     */
    private int determineSellQuantity(TradeRecommendation trade) {
        // 1. Sanity check: If trade data or stockPrediction is null, do nothing.
        if (trade == null
                || trade.getStockNewsAnalysis().getStockPrediction() == null
                || trade.getStockNewsAnalysis().getStockPrediction().getMarketSentiment() == null) {
            return 0;
        }

        var bullOrBearish = trade.getStockNewsAnalysis().getStockPrediction().getMarketSentiment();

        var ticker = trade.getStockNewsAnalysis().getTicker();
        // 2. If the outlook is Bullish, we hold.
        if (MarketSentiment.BULLISH.equals(bullOrBearish)) {
            return 0;
        }

        // 3. If the outlook is Bearish, calculate sell quantity based on risk factors
        if (MarketSentiment.BEARISH.equals(bullOrBearish)) {
            int baseSellQuantity = repository.findBaseSellQuantity(ticker); // Define your baseline share block size

            // Extract confidence or risk multiplier if available in tradePrediction
            double confidenceMultiplier = 1.0;
            if (trade.getTradePrediction() != null) {
                // Assuming getConfidence() returns a value between 0.0 and 1.0
                confidenceMultiplier = trade.getTradePrediction().getTradeConfidence();
            }

            // Scale the sell quantity dynamically based on signal strength
            int finalSellQuantity = (int) (baseSellQuantity * confidenceMultiplier);

            // Optional: Caps to ensure you don't over-sell past standard risk limits
            int maxSellLimit = repository.findMaxSellLimit(trade.getStockNewsAnalysis().getTicker());
            return Math.min(finalSellQuantity, maxSellLimit);
        }

        return 0;
    }

    public int determineBuyQuantity(TradeRecommendation tradeRecommendation) {

        // 1. Guard against null advice or missing trade prediction
        if (tradeRecommendation == null || tradeRecommendation.getTradePrediction() == null ) {
            return 0;
        }

        var stockMarketPrice = tradeRecommendation.getPrice();

        // 2. Safe to compare now
        if (stockMarketPrice == null || stockMarketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // 2. Calculate maximum cash allowed for this single trade
        var maxTradeCapital = this.repository.findTotalPortfolioValue().multiply(
                repository.findMaxAllocationPerTrade());

        // 3. Scale the capital based on the AI/News conviction score
        // If sentiment is 0.5, we only use 50% of our maximum allowed capital
        var allocatedCapital = maxTradeCapital.multiply(tradeRecommendation.getStockNewsAnalysis()
                .getStockPrediction().getSentimentConfidence());

        // 4. Calculate share quantity
        var targetQuantity = allocatedCapital.divide(stockMarketPrice,
                4, RoundingMode.HALF_UP
        );

        // 5. Return as a whole integer (rounding down to be safe)
        return targetQuantity.intValue();
    }

    public Iterable<PortfolioTradeProposal> findActiveTradeProposals() {
        return this.tradeRepository.findNonRejectTradeProposals();
    }

    public void acceptTradeProposalById(String id) {

        this.changeProposalStatusTradeProposalById(id, ProposalStatus.Accepted);
    }

    public void rejectTradeProposalById(String id) {
        this.changeProposalStatusTradeProposalById(id, ProposalStatus.Rejected);

    }

    private void changeProposalStatusTradeProposalById(String id, ProposalStatus proposalStatus) {

        // Guard clause for the input parameter
        if (id == null) {
            log.error("Cannot changed trade proposal because the provided ID is null.");
            return;
        }

        log.info("Accepting trade proposal with ID: {}", id);

        var optional = tradeRepository.findById(id);

        if (optional.isEmpty()) {
            log.error("Trade proposal entity with ID {} does exist", id);
            return;
        }
        var entity = optional.get();

        // Safely extract the proposal and its status using Optional to prevent NPEs
        var proposal = entity.getTradeProposal();
        if (proposal == null) {
            log.error("Trade proposal entity with ID {} exists, but contains a null TradeProposal object.", id);
            return;
        }

        proposal.setProposalStatus(proposalStatus);

        tradeRepository.save(entity);
        log.info("Trade proposal {} successfully accepted.", id);
    }
}
