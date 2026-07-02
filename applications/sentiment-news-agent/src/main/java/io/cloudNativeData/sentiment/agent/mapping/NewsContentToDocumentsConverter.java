package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.news.NewsContext;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NewsContentToDocumentsConverter implements Converter<NewsContext, List<Document>> {
    private final String modelName;
    private final JsonMapper jsonMapper;

    public NewsContentToDocumentsConverter(@Value("${app.news.content.model.name:sentiment}") String modelName, JsonMapper jsonMapper) {
        this.modelName = modelName;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public List<Document> convert(@NonNull NewsContext newsContent) {

//        Map<String, Object> metadata = Map.of(
//                "marketSentiment", prediction.getMarketSentiment().name(),
//                "sentimentConfidence", prediction.getSentimentConfidence().doubleValue(),
//                "modelName", modelName,
//                "newsSummary", prediction.getNewsSummary()); // duplicate if you need to extract it cleanly later

        var json = jsonMapper.writeValueAsString(newsContent);

        return List.of(
                Document.builder()
                        .text(json)
                        .build());

    }
}
