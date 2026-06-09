package io.cloudNativeData.portfolio.agent.function;

import io.cloudNativeData.portfolio.agent.service.ProposeTradeService;
import io.cloudNativeData.trading.PortfolioTradeProposal;
import io.cloudNativeData.trading.TradeRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposeTradeFunctionTest {

    private ProposeTradeFunction subject;

    @Mock
    private ProposeTradeService service;

    @Mock
    private TradeRecommendation tradeRecommendation;

    @BeforeEach
    void setUp() {
        subject = new ProposeTradeFunction(service);
    }

    @Test
    void apply() {

        var expected = PortfolioTradeProposal.builder().build();
        when(service.propose(any(TradeRecommendation.class)))
                .thenReturn(expected);

        var actual = subject.apply(tradeRecommendation);

        assertThat(actual).isEqualTo(expected);
    }
}