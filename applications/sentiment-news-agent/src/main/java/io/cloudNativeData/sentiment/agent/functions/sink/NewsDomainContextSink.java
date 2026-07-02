package io.cloudNativeData.sentiment.agent.functions.sink;

import io.cloudNativeData.trading.news.NewsContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsDomainContextSink implements Consumer<NewsContext> {

    private final VectorStore vectorStore;
    private final Converter<NewsContext,List<Document>> converter;


    @Override
    public void accept(NewsContext newsContext) {


        vectorStore.add(converter.convert(newsContext));

        log.info("Added News Domain Context: {}", newsContext);
    }
}
