package io.cloudNativeData.sentiment.agent.functions.sink;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewsDomainContextSink implements Consumer<String> {

    private final VectorStore vectorStore;

    @Override
    public void accept(String newsContext) {
        vectorStore.add(List.of(
                Document.builder().text(newsContext).build()));

        log.info("Added News Domain Context: {}", newsContext);
    }
}
