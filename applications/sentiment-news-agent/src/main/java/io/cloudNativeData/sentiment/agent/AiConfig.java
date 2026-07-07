package io.cloudNativeData.sentiment.agent;

import io.cloudNativeData.sentiment.agent.ai.StockAnalysisInference;
import io.cloudNativeData.trading.StockPrediction;
import io.cloudNativeData.trading.news.NewsParameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class AiConfig {

    private final String prompt = """
            Analyze the provided news text to determine its potential market impact on the specified stock ticker.
            ### Context
            - **Target Ticker:** {ticker}
            - **Raw News Content:** {rawNews}
            
            ### Analysis Guidelines
            1. **Market Sentiment:** Evaluate whether the news is Positive, Negative, or Neutral specifically for the target ticker. Do not give a general market sentiment if it doesn't affect this stock.
            2. **Sentiment Confidence:** Provide a confidence score between 0.00 (completely uncertain) and 1.00 (absolute certainty) based on the clarity and credibility of the impact implied by the text.
            3. **News Summary:** Write a concise, 1-2 sentence summary of the news highlighting *why* it impacts the stock.
            
            ### Output Constraints
            - Rely strictly on the provided text. Do not hallucinate or assume historical context not present in the prompt.
            - Do not include markdown code blocks (like ```json) or extra conversational text in your response.
            
            ONLY RESPONSE WITH VALID JSON
            """;
    private static final String defaultSystemPrompt = """
            You are a Wall Street News Parsing Agent
            """;

    @Value("${app.sentiment.context.rag.threshold:0.85}")
    private Double sentimentRagThreshold;


    @Bean
    List<Advisor> advisors(VectorStore vectorStore){

        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(sentimentRagThreshold)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();

        return List.of(retrievalAugmentationAdvisor);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder)
    {
        return builder.defaultSystem(defaultSystemPrompt).build();
    }

    @Bean
    StockAnalysisInference inference(ChatClient chatClient,
                                     ChatModel chatModel,
                                     List<Advisor> advisor) {
        return news -> {
            var results = chatClient.prompt(
                            PromptTemplate.builder()
                                    .template(prompt)
                                    .build().create(Map.of(
                                            "rawNews",news.rawNews(),
                                            "ticker",news.stockTicker()))
                    )
//                    .advisors(advisor) //removed still doing a semantic search
                    .call()
                    .entity(StockPrediction.class);

            if(results != null)
                results.setModelName(chatModel.getOptions().getModel());

            log.info("AI results: {}", results);
            return results;
        };
    }


}
