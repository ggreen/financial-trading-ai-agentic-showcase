package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.news.NewsContext;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NewsContentToDocumentsConverter implements Converter<NewsContext, List<Document>> {
    private final String modelName;

    public NewsContentToDocumentsConverter(@Value("${app.news.content.model.name:sentiment}") String modelName) {
        this.modelName = modelName;
    }

    @Override
    public List<Document> convert(@NonNull NewsContext newsContent) {

        var prediction = newsContent.stockPrediction();

        Map<String, Object> metadata = Map.of(
                "marketSentiment", prediction.getMarketSentiment().name(),
                "sentimentConfidence", prediction.getSentimentConfidence().doubleValue(),
                "modelName", modelName,
                "newsSummary", prediction.getNewsSummary()); // duplicate if you need to extract it cleanly later

        return List.of(new Document(newsContent.rawNews(), metadata));
    }
}
