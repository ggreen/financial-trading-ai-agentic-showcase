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
import org.apache.geode.cache.query.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

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
    CqQuery sellCqQuery(ClientCache clientCache, StockListenerConfig stockListenerConfig,
                        SellStockListener sellStockListener) throws CqException, CqExistsException, RegionNotFoundException
    {
        var queryService = clientCache.getQueryService();
        // Create CqAttribute using CqAttributeFactory
        CqAttributesFactory cqf = new CqAttributesFactory();

        // Create a listener and add it to the CQ attributes callback defined below
        cqf.addCqListener(sellStockListener);
        var cqa = cqf.create();
        // Name of the CQ and its query

        // Create the CqQuery
        log.info("Creating seller CQ '{}' with OQL: {}", stockListenerConfig.getSellerName(),
                stockListenerConfig.getSellerQuery());

        var cqQuery = queryService.newCq(stockListenerConfig.getSellerName(),
                stockListenerConfig.getSellerQuery(), cqa,false);

        // Execute CQ, getting the optional initial result set
        // Without the initial result set, the call is priceTracker.execute();
        cqQuery.execute();

        return cqQuery;

    }

    /**
     * Configures the ContinuousQueryListenerContainer to register and manage the CQ.
     */
    @Bean
    CqQuery buyCqQuery(ClientCache clientCache, StockListenerConfig stockListenerConfig,
                        BuyStockListener buyStockListener) throws CqException, CqExistsException, RegionNotFoundException
    {
        var queryService = clientCache.getQueryService();
        // Create CqAttribute using CqAttributeFactory
        CqAttributesFactory cqf = new CqAttributesFactory();

        // Create a listener and add it to the CQ attributes callback defined below
        cqf.addCqListener(buyStockListener);
        var cqa = cqf.create();
        // Name of the CQ and its query

        // Create the CqQuery
        log.info("Creating buyer CQ '{}' with OQL: {}", stockListenerConfig.getBuyerName(),
                stockListenerConfig.getBuyerQuery());

        var cqQuery = queryService.newCq(stockListenerConfig.getBuyerName(),
                stockListenerConfig.getBuyerQuery(), cqa,false);

        // Execute CQ, getting the optional initial result set
        // Without the initial result set, the call is priceTracker.execute();
        cqQuery.execute();

        return cqQuery;

    }

}

