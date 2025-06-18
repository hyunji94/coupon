package org.example.couponcore.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    //@Value("${spring.data.redis.host}")
    @Value("localhost")
    private String host;

    //@Value("${spring.data.redis.host}")
    @Value("6379")
    private int port;

    @Bean
    RedissonClient redissonClient(){
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }
}
