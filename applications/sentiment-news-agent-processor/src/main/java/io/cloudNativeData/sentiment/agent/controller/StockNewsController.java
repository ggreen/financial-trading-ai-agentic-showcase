package io.cloudNativeData.sentiment.agent.controller;

import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stocks")
@RequiredArgsConstructor
public class StockNewsController {

    private final StockNewsAnalyzerService service;
    private final Publisher<StockNewsAnalysis> publisher;

    public StockNewsAnalysis startNewsFlow(NewsParameters newsParameters) {

        var stockNewsAnalysis = service.analyze(newsParameters);
        publisher.send(stockNewsAnalysis);

        return stockNewsAnalysis;
    }
}
