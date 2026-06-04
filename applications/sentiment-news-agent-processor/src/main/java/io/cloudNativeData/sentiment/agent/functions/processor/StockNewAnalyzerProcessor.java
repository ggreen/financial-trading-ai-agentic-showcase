package io.cloudNativeData.sentiment.agent.functions.processor;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class StockNewAnalyzerProcessor implements Function<NewsParameters, StockNewsAnalysis> {

    private final StockAnalysisInference inference;

    @Override
    public StockNewsAnalysis apply(NewsParameters newsParameters) {
        var prediction =  inference.infer(newsParameters);

        return StockNewsAnalysis.builder()
                .prediction(prediction)
                .id(newsParameters.stockTicker())
                        .rawNews(newsParameters.rawNews()).build();
    }
}
