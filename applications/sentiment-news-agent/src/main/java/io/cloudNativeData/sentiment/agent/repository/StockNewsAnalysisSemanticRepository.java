package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.StockPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class StockNewsAnalysisSemanticRepository {

    private final VectorStore vectorStore;
    private final double similarityThreshold;
    private final Converter<Document,StockPrediction> documentToStockPredictionConverter;

    public StockNewsAnalysisSemanticRepository(VectorStore vectorStore,
                                               @Value("${app.stock.news.analysisa.semantic.repository.similarityThreshold:0.85}")double similarityThreshold,
                                               Converter<Document, StockPrediction> documentToStockPredictionConverter) {
        this.vectorStore = vectorStore;
        this.similarityThreshold = similarityThreshold;
        this.documentToStockPredictionConverter = documentToStockPredictionConverter;
    }


    public Optional<StockPrediction> findStockPredictionByRawNews(String rawNews) {

        // 1. Build the similarity search request
        var searchRequest  = SearchRequest.builder()
                .query(rawNews)
                .topK(1) // We only care about the best match
                .similarityThreshold(similarityThreshold)
                .build();

        log.info("searchRequest: {}", searchRequest);

        // 2. Query the Vector Database
        var documents = vectorStore.similaritySearch(searchRequest);

        log.info("Search results {}", documents);

        // 3. If no document passed the similarity threshold, return empty
        if (documents.isEmpty()) {
            return Optional.empty();
        }

        // 4. Map the top-matching document's metadata back into a StockPrediction
        return Optional.of(documentToStockPredictionConverter.convert(
                documents.getFirst()));
    }


}
