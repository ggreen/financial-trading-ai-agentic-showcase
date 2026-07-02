package io.cloudNativeData.research.trader.agent.mapping;

import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.pricing.StockPriceDto;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

@Component
public class StockPriceDtoToStockDailyPriceConverter implements Converter<StockPriceDto, StockDailyPrice> {
    @Override
    public StockDailyPrice convert(@NonNull StockPriceDto stockPriceDto) {

        var stockPriceLocalDate = Instant.ofEpochMilli(stockPriceDto.getUpdated())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return StockDailyPrice
                .builder()
                .id(stockPriceDto.getTicker()+"|"+stockPriceLocalDate)
                .ticker(stockPriceDto.getTicker())
                .volume(stockPriceDto.getVolume())
                .closePrice(BigDecimal.valueOf(stockPriceDto.getPrice()))
                .priceDate(
                        stockPriceLocalDate
                )
                .build();
    }
}
