package io.cloudNativeData.sentiment.agent.functions.processor;

import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class StockNewsAnalyzerProcessor implements Function<NewsParameters, StockNewsAnalysis> {

    private final StockNewsAnalyzerService service;

    @Override
    public StockNewsAnalysis apply(NewsParameters newsParameters) {

        return service.analyze(newsParameters);
    }
}
