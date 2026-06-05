package io.cloudNativeData.research.trader.agent;

import io.cloudNativeData.trading.StockDailyPrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;

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


}

