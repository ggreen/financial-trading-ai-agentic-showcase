package io.cloudNativeData.portfolio.agent.repository.entities;

import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.TradeStatus;
import io.cloudNativeData.trading.risk.RiskPrediction;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PortfolioTradeEntity {

    @Id
    private String id;
    private int quantity;
    @Embedded
    private TradeRecommendation tradeRecommendation;
    @Embedded
    private RiskPrediction riskPrediction;
    private TradeStatus tradeStatus;
}
