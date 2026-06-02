package io.cloudNativeData.sentiment.agent.functions;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class StockNewAnalyzerProcessor implements Function<String, StockNewsAnalysis> {

    private final StockAnalysisInference inference;

    @Override
    public StockNewsAnalysis apply(String rawNews) {
        return inference.infer(rawNews);
    }
}
