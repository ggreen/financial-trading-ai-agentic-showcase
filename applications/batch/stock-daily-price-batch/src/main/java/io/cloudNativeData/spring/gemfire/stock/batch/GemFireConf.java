package io.cloudNativeData.spring.gemfire.stock.batch;

import io.cloudNativeData.trading.StockDailyPrice;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@ClientCacheApplication
@EnableSecurity
@EnablePdx
@Configuration
@EnableGemfireRepositories
public class GemFireConf
{
    @Bean("StockDailyPrice")
    ClientRegionFactoryBean<String, StockDailyPrice> stockDailyPrice(ClientCache gemFireCache)
    {
        var bean = new ClientRegionFactoryBean<String,StockDailyPrice>();
        bean.setCache(gemFireCache);
        bean.setDataPolicy(DataPolicy.EMPTY);
        return bean;
    }
}
