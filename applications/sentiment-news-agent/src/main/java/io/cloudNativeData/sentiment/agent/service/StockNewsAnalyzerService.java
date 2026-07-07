package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.sentiment.agent.repository.StockNewsAnalysisRepository;
import io.cloudNativeData.sentiment.agent.repository.StockSemanticRepository;
import io.cloudNativeData.trading.news.NewsContext;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockNewsAnalyzerService {

    private final StockAnalysisInference inference;
    private final StockNewsAnalysisRepository stockNewsAnalysisRepository;
    private final StockSemanticRepository semanticRepository;
    private final static String semanticModelName = "semantic";

    /**
     * The analyzed news
     *
     * @param newsParameters the news parameters
     * @return the new analysis
     */
    public StockNewsAnalysis analyze(NewsParameters newsParameters) {

        log.info("Analyzing stock news information : " + newsParameters);

        log.info("Doing semantic query");
        var prediction = semanticRepository.findStockPredictionByRawNews(newsParameters.rawNews())
                .orElse(null);

        if (prediction == null) {
            log.info("No semantic query found, performing inference");
            prediction = inference.infer(newsParameters);
        }
        else {
            prediction.setModelName(semanticModelName);
        }

        log.info("prediction = {}", prediction);

        var stockNewsAnalysis = StockNewsAnalysis.builder()
                .stockPrediction(prediction)
                .id(newsParameters.stockTicker())
                .ticker(newsParameters.stockTicker())
                .rawNews(newsParameters.rawNews()).build();


        stockNewsAnalysisRepository.save(stockNewsAnalysis);

        log.info("Saved stockNewsAnalysis = {}", stockNewsAnalysis);

        return stockNewsAnalysis;
    }


    public Iterable<StockNewsAnalysis> findAllNews() {
        var newsList = new ArrayList<StockNewsAnalysis>(stockNewsAnalysisRepository.findAll());

        // Sort by ticker in memory
        return newsList.stream()
                .sorted(Comparator.comparing(StockNewsAnalysis::getTicker))
                .collect(Collectors.toList());
    }

    public void saveNewsContext(NewsContext newsContext) {
        semanticRepository.saveNewsContext(newsContext);
    }
}
