package io.cloudNativeData.sentiment.agent.service;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.sentiment.agent.repository.StockNewsAnalysisRepository;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockNewsAnalyzerService {

    private final StockAnalysisInference inference;
    private final StockNewsAnalysisRepository stockNewsAnalysisRepository;

    /**
     * The analyzed news
     * @param newsParameters the news parameters
     * @return the new analysis
     */
    public StockNewsAnalysis analyze(NewsParameters newsParameters) {
        var prediction =  inference.infer(newsParameters);

        var stockNewsAnalysis = StockNewsAnalysis.builder()
                .stockPrediction(prediction)
                .id(newsParameters.stockTicker())
                .ticker(newsParameters.stockTicker())
                .rawNews(newsParameters.rawNews()).build();

            stockNewsAnalysisRepository.save(stockNewsAnalysis);

            return stockNewsAnalysis;
    }


    public Iterable<StockNewsAnalysis> findAllNews() {
        var newsList = new ArrayList<StockNewsAnalysis>(stockNewsAnalysisRepository.findAll());

        // Sort by ticker in memory
        return newsList.stream()
                .sorted(Comparator.comparing(StockNewsAnalysis::getTicker))
                .collect(Collectors.toList());
    }
}
