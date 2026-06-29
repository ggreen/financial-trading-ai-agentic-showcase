package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.news.StockNewsAnalysis;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface StockNewsAnalysisRepository extends KeyValueRepository<StockNewsAnalysis, String> {
}
