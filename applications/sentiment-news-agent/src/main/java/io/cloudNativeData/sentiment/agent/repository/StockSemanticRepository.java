package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class StockSemanticRepository {

    private final VectorStore vectorStore;
    private final double similarityThreshold;
    private final NewsContextRepository newsContextRepository;

    public StockSemanticRepository(VectorStore vectorStore,
                                   @Value("${app.stock.news.analysisa.semantic.repository.similarityThreshold:0.85}")double similarityThreshold,
                                    NewsContextRepository newsContextRepository) {
        this.vectorStore = vectorStore;
        this.similarityThreshold = similarityThreshold;
        this.newsContextRepository = newsContextRepository;
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

        log.info("Search results {}", documents);
        var doc = documents.getFirst();
        var newContextId = doc.getText();

        log.info("Searching for context by news context Id");
        var newsContent = newsContextRepository.findById(Objects.requireNonNull(newContextId));

        return newsContent.map(NewsContext::getStockPrediction);

    }


    public void saveNewsContext(NewsContext newsContext) {

        this.vectorStore.add(List.of(Document.builder()
                .text(newsContext.getId())
                .build()));

        //TODO: Use Document metadata to store newscontent once Spring AI can retrieve meta with a search
        this.newsContextRepository.save(newsContext);
    }
}
