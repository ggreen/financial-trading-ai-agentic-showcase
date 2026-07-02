package io.cloudNativeData.research.trader.agent.controller;

import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.trading.TradeRecommendation;
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
class TradeControllerTest {


    @Mock
    private TradeRecommendationRepository repository;
    private final TradeRecommendation tradeRecommendation = JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();

    private TradeController subject;


    @BeforeEach
    void setUp() {
        subject = new TradeController(repository);
    }

    @Test
    void findAllTradeRecommendations() {
        var expected = List.of(tradeRecommendation);
        when(repository.findAll()).thenReturn(expected);

        var actual = subject.findAllTradeRecommendations();

        assertThat(actual).isEqualTo(expected);
    }
}