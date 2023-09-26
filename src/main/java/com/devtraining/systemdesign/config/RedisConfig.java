package com.devtraining.systemdesign.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Slf4j
@EnableRedisRepositories(
        basePackages = "com.devtraining.systemdesign.**.domain",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = KeyValueRepository.class))
@Configuration
public class RedisConfig {

    @ConfigurationProperties("spring.data.redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    public Lettuce lettuce() {
        return redisProperties().getLettuce();
    }

    // @Component
    // public static class RefreshTokenExpiredEventListener {
    //     @EventListener
    //     public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<RefreshToken> event) {
    //         RefreshToken expiredRefreshToken = (RefreshToken) event.getValue();
    //         assert expiredRefreshToken != null;
    //
    //         String key = expiredRefreshToken.getKey();
    //         String value = expiredRefreshToken.getValue();
    //         log.info("RefreshToken with key={}, value={} has expired", key, value);
    //     }
    // }
}
