package io.cloudNativeData.portfolio.agent.mapping;

import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PortfolioTradeProposalToEntityConverter implements Converter<PortfolioTradeProposal, PortfolioTradeEntity> {
    @Override
    public PortfolioTradeEntity convert(@NonNull PortfolioTradeProposal source) {
        return PortfolioTradeEntity.builder()
                .id(source.id())
                .tradeProposal(source)
                .build();
    }
}
