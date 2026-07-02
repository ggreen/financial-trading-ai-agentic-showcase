package io.cloudNativeData.research.trader.agent.service.faker;

import io.cloudNativeData.research.trader.agent.properties.StockPriceFakerConfig;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FakerStockPriceServiceTest {

    private FakerStockPriceService subject;
    private final StockPriceDto dto = JavaBeanGeneratorCreator.of(StockPriceDto.class).create();
    private final StockPriceFakerConfig config = StockPriceFakerConfig.builder()
            .lowVolume(4)
            .highVolume(10)
            .lowPrice(5.1)
            .highPrice(10.1)
            .currency("USD")
            .exchange("NASDAQ")
            .build();


    @BeforeEach
    void setUp() {
        subject = new FakerStockPriceService(config);
    }

    @Test
    void given_stock_then_generate_price() {
        var actual = subject.getCurrentStockPrice(dto.getTicker());

        assertThat(actual).isNotNull();

        assertThat(actual.getPrice()).isGreaterThanOrEqualTo(config.getLowPrice());
        assertThat(actual.getTicker()).isEqualTo(dto.getTicker());
        assertThat(actual.getCurrency()).isEqualTo(config.getCurrency());
        assertThat(actual.getName()).isEqualTo(dto.getTicker());
        assertThat(actual.getExchange()).isEqualTo(config.getExchange());
        assertThat(actual.getVolume()).isGreaterThanOrEqualTo(config.getLowVolume());
    }
}