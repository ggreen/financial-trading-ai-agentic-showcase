package io.cloudNativeData.portfolio.agent.repository;

import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioTradeRepository extends JpaRepository<PortfolioTradeEntity, String> {
}
