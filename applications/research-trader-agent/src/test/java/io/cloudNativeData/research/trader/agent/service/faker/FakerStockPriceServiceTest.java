package io.cloudNativeData.research.trader.agent.service.faker;

import io.cloudNativeData.research.trader.agent.properties.StockPriceFakerConfig;
import io.cloudNativeData.research.trader.agent.repository.StockPricingExecution;
import io.cloudNativeData.research.trader.agent.service.stocks.faker.FakerStockPriceService;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FakerStockPriceServiceTest {

    private FakerStockPriceService subject;
    @Mock
    private StockPricingExecution stockPricingExecution;
    private final StockPriceDto dto = JavaBeanGeneratorCreator.of(StockPriceDto.class).create();

    private final StockPriceFakerConfig config = StockPriceFakerConfig.builder()
            .lowVolume(4)
            .highVolume(10)
            .currency("USD")
            .exchange("NASDAQ")
            .build();


    @BeforeEach
    void setUp() {
        subject = new FakerStockPriceService(config,stockPricingExecution);
    }

    @Test
    void given_stock_then_generate_price() {
        BigDecimal expectedPrice = BigDecimal.TEN;

        when(stockPricingExecution.calculateMovingAverage200(anyString())).thenReturn(expectedPrice);

        var actual = subject.getCurrentStockPrice(dto.getTicker());

        assertThat(actual).isNotNull();

        assertThat(actual.getTicker()).isEqualTo(dto.getTicker());
        assertThat(actual.getCurrency()).isEqualTo(config.getCurrency());
        assertThat(actual.getName()).isEqualTo(dto.getTicker());
        assertThat(actual.getExchange()).isEqualTo(config.getExchange());
        assertThat(actual.getVolume()).isGreaterThanOrEqualTo(config.getLowVolume());
        assertThat(actual.getPrice()).isEqualTo(expectedPrice.doubleValue());
    }
}