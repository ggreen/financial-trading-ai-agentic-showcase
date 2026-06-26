package io.cloudNativeData.trading;

import io.cloudNativeData.trading.risk.RiskPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioTradeProposal{

    private String id;
    private int quantity;
    private TradeRecommendation tradeRecommendation;
    private RiskPrediction riskPrediction;
    private ProposalStatus proposalStatus;

}
