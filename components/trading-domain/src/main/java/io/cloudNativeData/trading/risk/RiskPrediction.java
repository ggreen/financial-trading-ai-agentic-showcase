package io.cloudNativeData.trading.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskPrediction{

    private RiskLevel riskLevel;
    private double riskConfidence;
    private String riskNotes;
    private String modelName;
}
