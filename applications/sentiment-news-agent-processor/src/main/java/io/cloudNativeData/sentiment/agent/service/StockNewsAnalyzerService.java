package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockNewsAnalyzerService {

    private final StockAnalysisInference inference;

    public StockNewsAnalysis analyze(NewsParameters newsParameters) {
        var prediction =  inference.infer(newsParameters);

        return StockNewsAnalysis.builder()
                .stockPrediction(prediction)
                .id(newsParameters.stockTicker())
                .ticker(newsParameters.stockTicker())
                .rawNews(newsParameters.rawNews()).build();
    }
}
