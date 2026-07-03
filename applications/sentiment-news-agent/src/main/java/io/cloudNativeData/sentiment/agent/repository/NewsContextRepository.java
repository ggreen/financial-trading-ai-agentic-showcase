package io.cloudNativeData.sentiment.agent.repository;

import io.cloudNativeData.trading.news.NewsContext;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsContextRepository extends KeyValueRepository<NewsContext,String> {
}
