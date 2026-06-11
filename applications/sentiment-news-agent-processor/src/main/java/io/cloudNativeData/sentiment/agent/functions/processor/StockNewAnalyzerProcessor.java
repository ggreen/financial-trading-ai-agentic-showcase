package io.cloudNativeData.sentiment.agent.functions.processor;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class StockNewAnalyzerProcessor implements Function<NewsParameters, StockNewsGeneration> {

    private final StockAnalysisInference inference;

    @Override
    public StockNewsGeneration apply(NewsParameters newsParameters) {
        var prediction =  inference.infer(newsParameters);

        return StockNewsGeneration.builder()
                .stockPrediction(prediction)
                .id(newsParameters.stockTicker())
                .ticker(newsParameters.stockTicker())
                        .rawNews(newsParameters.rawNews()).build();
    }
}
