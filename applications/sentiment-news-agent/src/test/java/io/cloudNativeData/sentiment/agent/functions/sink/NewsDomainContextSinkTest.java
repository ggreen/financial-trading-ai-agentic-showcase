package io.cloudNativeData.sentiment.agent.functions.sink;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsDomainContextSinkTest {

    private static final String news = """
            All good
            """;
    private NewsDomainContextSink subject;

    @Mock
    private VectorStore vectorStore;

    @BeforeEach
    void setUp() {
        subject = new NewsDomainContextSink(vectorStore);
    }

    @Test
    void given_news_when_accept_then_save_content() {
        subject.accept(news);

        verify(vectorStore).add(any());
    }
}