package io.cloudNativeData.portfolio.agent;

import io.cloudNativeData.portfolio.agent.ai.RiskInference;
import io.cloudNativeData.portfolio.agent.service.PortfolioAnalyticsService;
import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;
import io.cloudNativeData.trading.risk.RiskPrediction;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
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

//    @Bean
//    public SyncMcpToolCallbackProvider syncMcpToolCallbackProvider(List<McpSyncClient> mcpClients) {
//        return SyncMcpToolCallbackProvider.builder()
//                .mcpClients(mcpClients)
//                .build();
//    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.defaultSystem(defaultSystemPrompt).build();
    }

    @Bean
    RiskInference tradeRiskInference(ChatClient chatClient, ChatModel chatModel, List<Advisor> advisors) {
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

            var riskPrediction = chatClient.prompt(prompt)
                    .advisors(advisors)
                    .call()
                    .entity(RiskPrediction.class);

            if(riskPrediction != null)
                riskPrediction.setModelName(chatModel.getDefaultOptions().getModel());

            return riskPrediction;
        };
    }


    @Bean
    PortfolioAnalyticsService portfolioAnalyticsService(RestTemplate restTemplate,
                                                        @Value("${app.portfolio.analytics.service.url}")String url){
        return question ->  {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Define the raw string payload
            String requestBody = "Average stock price.";

            // 3. Wrap headers and body into an HttpEntity
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // 4. Make the POST request and map the JSON response to your record
            PortfolioQueryRequests response = restTemplate.postForObject(
                    url,
                    entity,
                    PortfolioQueryRequests.class
            );

            log.info("PortfolioQueryRequestsresponse: {}", response);
            return response;
        };
    }
}
