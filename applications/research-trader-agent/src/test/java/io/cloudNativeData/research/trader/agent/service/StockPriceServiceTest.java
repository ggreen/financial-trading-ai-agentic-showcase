package io.cloudNativeData.research.trader.agent.service;

import io.cloudNativeData.research.trader.agent.properties.StockApiProperties;
import io.cloudNativeData.research.trader.agent.service.stocks.GetStockFallbackService;
import io.cloudNativeData.research.trader.agent.service.stocks.StockPriceService;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockPriceServiceTest {


    private StockPriceService subject;
    @Mock
    private RestClient.Builder restBuilder;
    @Mock
    private CircuitBreaker readCircuitBreaker;
    @Mock
    private GetStockFallbackService fallbackService;

    private final StockPriceDto dto = JavaBeanGeneratorCreator
            .of(StockPriceDto.class).create();

    private final StockApiProperties properties = JavaBeanGeneratorCreator.of(StockApiProperties.class).create();

    @Mock
    private RestClient restClient;


    @Test
    void getFromRest() {

        when(restBuilder.baseUrl(anyString())).thenReturn(restBuilder);
        when(restBuilder.build()).thenReturn(restClient);

        subject = new StockPriceService(restBuilder, properties, readCircuitBreaker,
                fallbackService);

        when(readCircuitBreaker.run(any(), any())).thenReturn(dto);

        var actual = subject.getCurrentStockPrice(dto.getTicker());

        assertThat(actual).isEqualTo(dto);
    }

}