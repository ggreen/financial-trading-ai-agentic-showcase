package io.cloudNativeData.sentiment.agent.mapping;

import io.cloudNativeData.trading.MarketSentiment;
import io.cloudNativeData.trading.StockPrediction;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DocumentToStockPredictionConverter
        implements Converter<Document, StockPrediction> {

    @Override
    public StockPrediction convert(@NonNull Document document) {
        var metadata = document.getMetadata();
        StockPrediction prediction = new StockPrediction();

        // 1. Map Market Sentiment (String -> Enum)
        if (metadata.get("marketSentiment") != null) {
            String sentimentStr = (String) metadata.get("marketSentiment");
            prediction.setMarketSentiment(MarketSentiment.valueOf(sentimentStr));
        }

        // 2. Map Sentiment Confidence Safely (Number -> BigDecimal)
        // Vector stores often deserialize floating points as Double or Float.
        // Casting to Number first avoids a ClassCastException.
        if (metadata.get("sentimentConfidence") != null) {
            Number confidenceNum = (Number) metadata.get("sentimentConfidence");
            prediction.setSentimentConfidence(BigDecimal.valueOf(confidenceNum.doubleValue()));
        }

        // 3. Map Text Strings
        prediction.setNewsSummary((String) metadata.get("newsSummary"));
        prediction.setModelName((String) metadata.get("modelName"));

        return prediction;
    }
}
