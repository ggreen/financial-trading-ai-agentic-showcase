package io.cloudNativeData.trading.news;


import io.cloudNativeData.trading.StockPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsContext{
    private String id;
    private StockPrediction stockPrediction;
}
