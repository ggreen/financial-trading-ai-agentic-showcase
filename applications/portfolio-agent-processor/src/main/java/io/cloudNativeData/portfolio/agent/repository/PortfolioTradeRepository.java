package io.cloudNativeData.portfolio.agent.repository;

import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioTradeRepository extends JpaRepository<PortfolioTradeEntity, String> {

    @Query(value = "SELECT p.payload FROM portfolio.portfolio_trade_entity p", nativeQuery = true)
    Iterable<PortfolioTradeProposal> findAllTradeProposals();
}
