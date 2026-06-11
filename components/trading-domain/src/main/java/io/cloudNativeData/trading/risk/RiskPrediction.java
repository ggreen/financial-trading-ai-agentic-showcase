package io.cloudNativeData.trading.risk;

import lombok.Builder;

@Builder
public record RiskPrediction(RiskLevel riskLevel, double riskConfidence, String riskNotes) {

}
