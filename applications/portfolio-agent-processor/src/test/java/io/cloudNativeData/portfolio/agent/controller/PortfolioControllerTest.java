package io.cloudNativeData.portfolio.agent.controller;

import io.cloudNativeData.portfolio.agent.service.PortfolioService;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    private PortfolioController subject;

    @Mock
    private PortfolioService service;
    private final PortfolioTradeProposal proposal = JavaBeanGeneratorCreator.of(PortfolioTradeProposal.class)
            .create();

    @BeforeEach
    void setUp() {
        subject = new PortfolioController(service);
    }

    @Test
    void selectPortfolios() {
        var expected  = List.of(proposal);

        when(service.findActiveTradeProposals()).thenReturn(expected);

        var actual = subject.getTradeActiveProposals();

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    @DisplayName("Given a proposal id when accept, then change status to accept")
    void accept() {

        subject.acceptTradeProposal(proposal.getId());

        verify(service).acceptTradeProposalById(anyString());
    }


    @Test
    void reject() {

        subject.rejectTradeProposal(proposal.getId());

        verify(service).rejectTradeProposalById(anyString());
    }


    @Test
    void askQuestion() {
        var question = "What is the meaning of life";

        var expected = JavaBeanGeneratorCreator.of(PortfolioQueryRequests.class).create();

        when(service.askAnalytics(anyString())).thenReturn(expected);

        var actual = subject.askAnalytics(question);

        assertThat(actual).isEqualTo(expected);
    }
}