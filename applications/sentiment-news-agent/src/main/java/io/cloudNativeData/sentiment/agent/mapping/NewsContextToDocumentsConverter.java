package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.news.NewsContext;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NewsContextToDocumentsConverter implements Converter<NewsContext, List<Document>> {
    private final String modelName;

    public NewsContextToDocumentsConverter(@Value("${app.news.content.model.name:sentiment}") String modelName) {

        this.modelName = modelName;
    }

    @Override
    public List<Document> convert(@NonNull NewsContext newsContent) {

        return List.of(
                Document.builder().text(newsContent.getId()).build()
                );
    }
}
