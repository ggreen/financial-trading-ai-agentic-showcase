package io.cloudNativeData.sentiment.agent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
public class ValkeyConfig {

    @Bean
    JedisConnectionFactory redisConnectionFactory()
    {
        return new JedisConnectionFactory();
    }
}
