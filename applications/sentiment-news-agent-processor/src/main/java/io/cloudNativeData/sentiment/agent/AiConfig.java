package io.cloudNativeData.sentiment.agent;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.StockNewsAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class AiConfig {

    private final String prompt = """
            Analyze the text for financial market impact. "
            Respond ONLY in valid JSON")
            """;
    private static final String defaultSystemPrompt = """
            You are a Wall Street News Parsing Agent
            """;


    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.defaultSystem(defaultSystemPrompt).build();
    }

    @Bean
    StockAnalysisInference inference(ChatClient chatClient,
                                     List<Advisor> advisor) {
        return news -> {
            var results = chatClient.prompt(prompt)
                    .advisors(advisor)
                    .call()
                    .entity(StockNewsAnalysis.class);

            log.info("AI results: {}", results);
            return results;
        };
    }
}
