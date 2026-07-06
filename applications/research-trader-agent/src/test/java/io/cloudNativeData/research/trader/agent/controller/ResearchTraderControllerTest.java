package io.cloudNativeData.research.trader.agent.controller;

import io.cloudNativeData.research.trader.agent.repository.TradeRecommendationRepository;
import io.cloudNativeData.research.trader.agent.service.ResearchTraderService;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import io.cloudNativeData.trading.watch.StockPriceMovingAverageWatchList;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResearchTraderControllerTest {

    @Mock
    private TradeRecommendationRepository repository;
    private final TradeRecommendation tradeRecommendation = JavaBeanGeneratorCreator.of(TradeRecommendation.class).create();

    @Mock
    private ResearchTraderService researchTraderService;

    private ResearchTraderController subject;


    @BeforeEach
    void setUp() {
        subject = new ResearchTraderController(repository,researchTraderService);
    }

    @Test
    void findAllTradeRecommendations() {
        var expected = List.of(tradeRecommendation);
        when(repository.findAll()).thenReturn(expected);

        var actual = subject.findAllTradeRecommendations();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getStockPriceMovingAverageWatchList() {
        StockPriceMovingAverageWatchList expected = JavaBeanGeneratorCreator.of(StockPriceMovingAverageWatchList.class).create();
        when(researchTraderService.getStockPriceMovingAverageWatchList()).thenReturn(expected);
        // Act
        var actual = subject.getStockPriceMovingAverageWatchList();

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void saveStockPriceMovingAverage() {
        // Arrange
        StockPriceMovingAverage inputDto = JavaBeanGeneratorCreator.of(StockPriceMovingAverage.class).create();

        // Act
        subject.saveStockPriceMovingAverage(inputDto);

        // Assert
        // Verify that the service method was called exactly once with the correct object
        verify(researchTraderService, times(1)).saveStockMovingAverage(inputDto);
    }
}