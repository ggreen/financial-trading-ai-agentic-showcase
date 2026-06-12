package io.cloudNativeData.portfolio.agent.mapping;

import io.cloudNativeData.portfolio.agent.repository.entities.PortfolioTradeEntity;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PortfolioTradeProposalToEntityConverterTest {

    private final PortfolioTradeProposalToEntityConverter subject = new PortfolioTradeProposalToEntityConverter();
    private final @NonNull PortfolioTradeProposal proposal = JavaBeanGeneratorCreator.of(PortfolioTradeProposal.class).create();

    @Test
    void convert() {
        var expected = PortfolioTradeEntity.builder().id(proposal.id())
                .tradeProposal(proposal)
                .build();

        var actual = subject.convert(proposal);

        assertThat(actual).isEqualTo(expected);
    }
}