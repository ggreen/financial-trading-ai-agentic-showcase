package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.research.trader.agent.ai.TradePredictionInference;
import io.cloudNativeData.trading.TradePrediction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class AiConfig {

    private static final String defaultSystemPrompt = """
            You are a Quantitative Research Agent. Based on technical metrics and sentiment
            """;
    private static final String prompt = """
            calculate trade targets based  
            moving Average: {movingAverage200}
            market sentiment: {marketSentiment}
            market sentiment confidence: {marketSentimentConfidence}
             
            Output JSON: 'ticker', 'action' (BUY/SELL), 'targetPrice', 'quantity'
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.defaultSystem(defaultSystemPrompt).build();
    }

    @Bean
    TradePredictionInference tradePredictionInference(ChatClient chatClient, List<Advisor> advisors) {
        return tradeParameters ->  {

            var promptTemplate = new PromptTemplate(prompt);
            var prompt = promptTemplate
                    .create(Map.of(
                            "movingAverage200", tradeParameters.getMovingAverage200(),
                            "marketSentiment", tradeParameters.getPrediction().getMarketSentiment(),
                            "marketSentimentConfidence",tradeParameters.getPrediction().getSentimentConfidence())
                    );

            return chatClient.prompt(prompt)
                    .advisors(advisors)
                    .call()
                    .entity(TradePrediction.class);
        };
    }
}
