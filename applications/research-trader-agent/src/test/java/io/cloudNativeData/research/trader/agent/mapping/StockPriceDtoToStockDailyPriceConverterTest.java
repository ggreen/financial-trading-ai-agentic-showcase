package io.cloudNativeData.research.trader.agent.mapping;

import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Text;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StockPriceDtoToStockDailyPriceConverterTest {

    private final StockPriceDto dto = JavaBeanGeneratorCreator.of(StockPriceDto.class).create();
    private final StockPriceDtoToStockDailyPriceConverter subject = new StockPriceDtoToStockDailyPriceConverter();

    @Test
    void convert() {

        var now = System.currentTimeMillis();
        dto.setUpdated(now);

        String dateText = Text.format().formatDate("yyyy-MM-dd",new Date(dto.getUpdated()));

        StockDailyPrice expected = StockDailyPrice
                .builder()
                .id(dto.getTicker()+"|"+dateText)
                .ticker(dto.getTicker())
                .volume(dto.getVolume())
                .closePrice(BigDecimal.valueOf(dto.getPrice()))
                .priceDate(Instant.ofEpochMilli(now)
                        .atZone(ZoneId.systemDefault()).toLocalDate())
                .build();

        var actual = subject.convert(dto);
        assertThat(actual).isNotNull();
        assertThat(actual.getPriceDate()).isEqualTo(expected.getPriceDate());
        assertThat(actual.getVolume()).isEqualTo(expected.getVolume());
        assertThat(actual.getClosePrice()).isEqualTo(expected.getClosePrice());
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTicker()).isEqualTo(expected.getTicker());

        assertThat(actual).isEqualTo(expected);
    }
}