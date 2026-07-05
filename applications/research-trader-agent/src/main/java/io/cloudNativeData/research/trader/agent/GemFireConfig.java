package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.research.trader.agent.listener.BuyStockListener;
import io.cloudNativeData.research.trader.agent.listener.SellStockListener;
import io.cloudNativeData.research.trader.agent.properties.StockListenerConfig;
import io.cloudNativeData.trading.StockDailyPrice;
import io.cloudNativeData.trading.TradeRecommendation;
import io.cloudNativeData.trading.pricing.StockPriceMovingAverage;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.data.gemfire.listener.ContinuousQueryDefinition;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;

@Configuration
@ClientCacheApplication(subscriptionEnabled = true, readyForEvents = true)
@EnableConfigurationProperties(StockListenerConfig.class)
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
    public ContinuousQueryListenerContainer sellListener(ClientCache clientCache,
                                                         StockListenerConfig stockListenerConfig,
                                                         SellStockListener sellStockListener) {

        log.info("Creating seller listener with conf: {}", stockListenerConfig);

        ContinuousQueryListenerContainer container = new ContinuousQueryListenerContainer();
        container.setCache(clientCache);

        // Define the CQ definition (Name, Query String, and the Listener)
        var cqDefinition = new ContinuousQueryDefinition(
                stockListenerConfig.getSellerName(),
                stockListenerConfig.getSellerQuery(),
                sellStockListener,
                false // durable flag
        );

        // Register the definition to the container
        container.addContinuousQueryDefinition(cqDefinition);

        return container;
    }

    /**
     * Configures the ContinuousQueryListenerContainer to register and manage the CQ.
     */
    @Bean
    public ContinuousQueryListenerContainer buyListener(StockListenerConfig stockListenerConfig,
                                                        ClientCache clientCache,
                                                        BuyStockListener buyStockListener) {

        log.info("Creating buyer listener with conf: {}", stockListenerConfig);

        ContinuousQueryListenerContainer container = new ContinuousQueryListenerContainer();
        container.setCache(clientCache);


        // Define the CQ definition (Name, Query String, and the Listener)
        var cqDefinition = new ContinuousQueryDefinition(
                stockListenerConfig.getBuyerName(),
                stockListenerConfig.getBuyerQuery(),
                buyStockListener,
                false // durable flag
        );

        // Register the definition to the container
        container.addContinuousQueryDefinition(cqDefinition);

        return container;
    }
}

