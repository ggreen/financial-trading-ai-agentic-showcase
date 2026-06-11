package io.cloudNativeData.sentiment.agent.controller;

import io.cloudNativeData.sentiment.agent.service.StockNewsAnalyzerService;
import io.cloudNativeData.trading.news.NewsParameters;
import io.cloudNativeData.trading.news.StockNewsAnalysis;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.patterns.integration.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockNewsControllerTest {

    @Mock
    private StockNewsAnalyzerService service;

    @Mock
    private Publisher<StockNewsAnalysis> publisher;

    private StockNewsController subject;
    private final NewsParameters newsParameters = JavaBeanGeneratorCreator.of(NewsParameters.class).create();
    private final StockNewsAnalysis expectedNewsAnalysis = JavaBeanGeneratorCreator.of(StockNewsAnalysis.class).create();

    @BeforeEach
    void setUp() {
        subject = new StockNewsController(service,publisher);
    }

    @Test
    void startTradePipeline() {

        when(service.analyze(any())).thenReturn(expectedNewsAnalysis);

        var actual = subject.startNewsFlow(newsParameters);

        assertThat(actual).isEqualTo(expectedNewsAnalysis);

        verify(publisher).send(any());
    }
}