package io.cloudNativeData.sentiment.agent.controller;

import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.integration.Publisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("stocks")
@RequiredArgsConstructor
@Slf4j
public class StockNewsController {

    private final StockNewsAnalyzerService service;
    private final Publisher<StockNewsAnalysis> publisher;

    @PostMapping
    public StockNewsAnalysis startNewsFlow(@RequestBody NewsParameters newsParameters) {

        log.info("startNewsFlow newsParameters = {}", newsParameters);

        var stockNewsAnalysis = service.analyze(newsParameters);
        publisher.send(stockNewsAnalysis);

        return stockNewsAnalysis;
    }



    @GetMapping
    public Iterable<StockNewsAnalysis> findAll() {
        return this.service.findAllNews();
    }
}
