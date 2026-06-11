package io.cloudNativeData.portfolio.agent;

import io.cloudNativeData.portfolio.agent.ai.RiskInference;
import io.cloudNativeData.trading.risk.RiskPrediction;
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
            You are stock portfolio agent
            """;
    private static final String prompt = """
            calculate the risk of a target based  
            tradeAction: {tradeAction}
            quantity: {quantity}
            marketSentiment: {marketSentiment}
            sentimentConfidence: {sentimentConfidence}
            newsSummary: {newsSummary}
            Output JSON: 'riskLevel' (HIGH/MEDIUM/LOW), 'riskConfidence', 'riskNotes'
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.defaultSystem(defaultSystemPrompt).build();
    }

    @Bean
    RiskInference tradeRiskInference(ChatClient chatClient, List<Advisor> advisors) {
        return riskParameters ->  {

            var promptTemplate = new PromptTemplate(prompt);
            var prompt = promptTemplate
                    .create(Map.of(
                            "tradeAction", riskParameters.tradeAction(),
                            "quantity", riskParameters.quantity(),
                            "marketSentiment",riskParameters.stockPrediction().getMarketSentiment(),
                            "sentimentConfidence",riskParameters.stockPrediction().getSentimentConfidence(),
                           "newsSummary", riskParameters.newsSummary())
                    );

            return chatClient.prompt(prompt)
                    .advisors(advisors)
                    .call()
                    .entity(RiskPrediction.class);
        };
    }
}
