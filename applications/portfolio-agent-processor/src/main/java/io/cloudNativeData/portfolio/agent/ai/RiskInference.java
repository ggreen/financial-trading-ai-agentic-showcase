package io.cloudNativeData.portfolio.agent.ai;

import io.cloudNativeData.trading.risk.RiskPrediction;
import io.cloudNativeData.trading.risk.TradeRiskParameters;

public interface RiskInference {
    RiskPrediction predict(TradeRiskParameters parameters);
}
