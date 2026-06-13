package io.cloudNativeData.portfolio.agent.controller;

import io.cloudNativeData.portfolio.agent.repository.PortfolioTradeRepository;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    private PortfolioController subject;
    @Mock
    private PortfolioTradeRepository repository;
    private PortfolioTradeProposal proposal = JavaBeanGeneratorCreator.of(PortfolioTradeProposal.class)
            .create();

    @BeforeEach
    void setUp() {
        subject = new PortfolioController(repository);
    }

    @Test
    void selectPortfolios() {
        var expected  = List.of(proposal);

        when(repository.findAllTradeProposals()).thenReturn(expected);

        var actual = subject.getTradeProposals();

        assertThat(actual).isEqualTo(expected);
    }
}