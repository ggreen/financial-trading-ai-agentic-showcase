package io.cloudNativeData.portfolio.sql.analytics.mcp.service;

import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("analytics")
@Slf4j
@RequiredArgsConstructor
public class SqlExecutorMcpService {

    private final JdbcTemplate jdbcTemplate;
    private final Converter<String,String> generateSqlFromText;

    /**
     * This annotation registers the method as an MCP tool available to the client.
     */
//    @Tool(description = "Converts natural language text into a SQL query, executes it against the database, and returns the results.")
    @PostMapping
    @RequestMapping("asks")
    public PortfolioQueryRequests executeTextAsSql(@RequestBody String textPrompt) {

        // 1. Generate the SQL from the text prompt using an LLM
        String generatedSql = generateSqlFromText.convert(textPrompt);

        log.info("Generated sql: {}", generatedSql);

        var resultsBuilder = PortfolioQueryRequests.builder();

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
}