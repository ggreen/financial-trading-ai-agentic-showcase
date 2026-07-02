package io.cloudNativeData.portfolio.agent.repository.entities;

import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.TradeStatus;
import io.cloudNativeData.trading.risk.RiskPrediction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "portfolio")
public class PortfolioTradeEntity {

    @Id
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    PortfolioTradeProposal tradeProposal;

}
