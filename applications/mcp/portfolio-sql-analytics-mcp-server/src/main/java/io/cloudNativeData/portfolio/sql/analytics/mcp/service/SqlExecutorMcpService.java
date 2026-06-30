package io.cloudNativeData.portfolio.sql.analytics.mcp.service;

import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*

payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' ->> 'rawNews' rawNews

 select id,
     payload->>'quantity' quantity,
     payload->>'proposalEpoch' proposalEpoch,
     payload-> 'tradeRecommendation' -> 'tradePrediction' ->>  'adviceAction' adviceAction,
     payload-> 'tradeRecommendation' -> 'tradePrediction' ->>  'tradeConfidence' tradeConfidence,
     payload-> 'tradeRecommendation' -> 'tradePrediction' ->>  'modelName' tradePredictionModelName,
     payload-> 'tradeRecommendation' ->>  'price' price,
     payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' ->> 'ticker' ticker,
     payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' -> 'stockPrediction' ->> 'marketSentiment' marketSentiment,
     payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' -> 'stockPrediction' ->> 'sentimentConfidence' sentimentConfidence,
     payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' -> 'stockPrediction' ->> 'modelName' stockPredictionModelName,
     payload-> 'tradeRecommendation' -> 'stockNewsAnalysis' -> 'stockPrediction' ->> 'newsSummary' newsSummary,
     payload-> 'riskPrediction' ->> 'riskLevel' riskLevel,
     payload-> 'riskPrediction' ->> 'riskNotes' riskNotes,
     payload-> 'riskPrediction' ->> 'modelName' riskModelName
 from portfolio.portfolio_trade_entity;

  */

@Service
@RestController
@Slf4j
public class SqlExecutorMcpService {

    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;

    private static final String systemInstructions = """
            You are an expert PostgreSQL Text-to-SQL assistant. Your sole task is to translate natural language user questions into syntactically correct, highly efficient 
            PostgreSQL queries utilizing the database schema provided below.
            You are a strict text-to-SQL translator.
            Given a user request, output ONLY the valid SQL query that satisfies it. 
            Do not include any conversational text, explanations, or markdown formatting.
                
                ### DATABASE SCHEMA INFORMATION
                You have access to a single flattened analytics view named `portfolio.v_portfolio_trade_analytics`. 
                Do NOT query any underlying tables; always query this view.
                
                Here is the DDL for the view, including the exact column names and data types:
                - id (UUID / Primary Key equivalent)
                - quantity (NUMERIC) -> The number of shares/units proposed
                - proposal_epoch (BIGINT) -> Unix epoch timestamp of the proposal
                - advice_action (TEXT) -> e.g., 'BUY', 'SELL', 'HOLD'
                - trade_confidence (NUMERIC) -> Model confidence score for the trade recommendation
                - trade_prediction_model_name (TEXT) -> Name of the ML model that handled trade prediction
                - price (NUMERIC) -> Asset price at the time of recommendation
                - ticker (TEXT) -> Stock ticker symbol (e.g., 'AAPL', 'TSLA')
                - market_sentiment (TEXT) -> Sentiment based on news (e.g., 'BULLISH', 'BEARISH', 'NEUTRAL')
                - sentiment_confidence (NUMERIC) -> Model confidence score for the news sentiment
                - stock_prediction_model_name (TEXT) -> Name of the ML model that analyzed the news
                - news_summary (TEXT) -> Text summary of the stock news analysis
                - risk_level (TEXT) -> Evaluated risk (e.g., 'HIGH', 'MEDIUM', 'LOW')
                - risk_notes (TEXT) -> Detailed textual notes regarding risk factors
                - risk_model_name (TEXT) -> Name of the ML model that evaluated risk
                
                ### RULES & CONSTRAINTS
                1. ONLY return the executable SQL code inside a standard markdown code block (```sql ... ```). Do not include any explanations, introductory text, or markdown prose outside the code block.
                2. Use valid PostgreSQL syntax.\s
                3. Pay strict attention to column data types. For example:
                   - For string matches (like `ticker`), use case-insensitive matching (`ILIKE`) or ensure correct capitalization if specified.
                   - For numeric filters (like `price` or `trade_confidence`), use standard comparison operators (`>`, `<`, `=`).
                4. If the user asks for a time-based query, remember that `proposal_epoch` is a BIGINT Unix timestamp. Use `to_timestamp(proposal_epoch)` to compare with dates if necessary.
                5. Never perform destructive commands (INSERT, UPDATE, DELETE, DROP). Only generate SELECT queries.
                6. Limit the results to 100 rows using `LIMIT 100` unless the user explicitly requests a different amount or an aggregation.
                
                ### EXAMPLE CONVERSIONS
                User: "Show me all high risk buy recommendations for Apple stock."
                SQL: SELECT * FROM portfolio.v_portfolio_trade_analytics\s
                WHERE ticker = 'AAPL'\s
                  AND advice_action = 'BUY'\s
                  AND risk_level = 'HIGH';
                  
               User: "What is the average trade confidence grouped by the stock prediction model name?"
               SQL: SELECT stock_prediction_model_name, AVG(trade_confidence) AS avg_trade_confidence
                   FROM portfolio.v_portfolio_trade_analytics
                   GROUP BY stock_prediction_model_name;
            """;

    public SqlExecutorMcpService(JdbcTemplate jdbcTemplate, ChatClient.Builder chatClientBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        // Building a chat client specifically configured to output raw SQL
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * This annotation registers the method as an MCP tool available to the client.
     */
    @Tool(description = "Converts natural language text into a SQL query, executes it against the database, and returns the results.")
    @PostMapping
    public PortfolioQueryRequests executeTextAsSql(@RequestBody String textPrompt) {

        // 1. Generate the SQL from the text prompt using an LLM
        String generatedSql = generateSqlFromText(textPrompt);

        log.info("Generated sql: {}", generatedSql);

        // Clean up the markdown code blocks if the LLM wraps it in ```sql ... ```
        generatedSql = cleanSqlCodeBlocks(generatedSql);

        var resultsBuilder = PortfolioQueryRequests.builder();

        log.info("Cleaned sql: {}", generatedSql);

        resultsBuilder.sql(generatedSql);

        // 2. Execute the SQL via JdbcTemplate and return the List of Maps
        try {
            var results = jdbcTemplate.queryForList(generatedSql);
            log.info("Cleaned results: {}", results);
            resultsBuilder.results(results);

        } catch (Exception e) {

            log.info("Error results: {}", e);
            List<Map<String,Object>> resultsError =  List.of(Map.of("error", "Failed to execute query: " + e.getMessage()));

            resultsBuilder.results(resultsError);
        }

        return resultsBuilder.build();
    }


    private String generateSqlFromText(String prompt) {

        return chatClient.prompt()
                .system(systemInstructions)
                .user(prompt)
                .call()
                .content();
    }

    private String cleanSqlCodeBlocks(String sql) {
        if (sql == null) return "";
        return sql.replaceAll(".*```sql", "")
                .replaceAll("```", "")
                .trim();
    }
}