package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.research.trader.agent.listener.SellStockListener;
import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.data.gemfire.listener.ContinuousQueryDefinition;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;

@Configuration
@ClientCacheApplication(subscriptionEnabled = true)
@EnableGemfireFunctionExecutions
@Slf4j
public class GemFireConfig {

    @Bean("StockDailyPrice")
    ClientRegionFactoryBean<String, StockDailyPrice> stockDailyPrice(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, StockDailyPrice>();
        regionBean.setCache(cache);
        regionBean.setName("StockDailyPrice");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

    //StockPrice
    @Bean("StockPrice")
    ClientRegionFactoryBean<String, StockDailyPrice> stockPrice(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, StockDailyPrice>();
        regionBean.setCache(cache);
        regionBean.setName("StockPrice");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

    @Bean("TradeRecommendation")
    ClientRegionFactoryBean<String, TradeRecommendation> tradeRecommendation(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, TradeRecommendation>();
        regionBean.setCache(cache);
        regionBean.setName("TradeRecommendation");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }

    @Bean("StockPriceMovingAverage")
    ClientRegionFactoryBean<String, StockPriceMovingAverage> tockPriceMovingAverage(ClientCache cache) {
        var regionBean = new ClientRegionFactoryBean<String, StockPriceMovingAverage>();
        regionBean.setCache(cache);
        regionBean.setName("StockPriceMovingAverage");
        regionBean.setDataPolicy(DataPolicy.EMPTY);
        return regionBean;
    }


    /**
     * Configures the ContinuousQueryListenerContainer to register and manage the CQ.
     */
    @Bean
    public ContinuousQueryListenerContainer continuousQueryListenerContainer(ClientCache clientCache,
                                                                             SellStockListener sellStockListener) {

        ContinuousQueryListenerContainer container = new ContinuousQueryListenerContainer();
        container.setCache(clientCache);

        // Define your OQL query. For example, selecting from the /StockPrice region
        String oqlQuery = "SELECT * FROM /StockPrice WHERE status = 'SELL'"; // Adjust your OQL predicate accordingly

        // Define the CQ definition (Name, Query String, and the Listener)
        var cqDefinition = new ContinuousQueryDefinition(
                "SellStockCQ",
                oqlQuery,
                sellStockListener,
                false // durable flag
        );

        // Register the definition to the container
        container.addContinuousQueryDefinition(cqDefinition);

        return container;
    }
}

