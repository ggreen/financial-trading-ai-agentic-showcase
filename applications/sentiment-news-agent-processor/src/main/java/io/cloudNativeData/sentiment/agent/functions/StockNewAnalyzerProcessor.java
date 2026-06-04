package io.cloudNativeData.sentiment.agent.functions;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
