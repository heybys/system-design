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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Slf4j
@EnableRedisRepositories(
        basePackages = "com.devtraining.systemdesign.**.domain",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = KeyValueRepository.class),
        enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP)
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

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.addMessageListener(
                (message, pattern) -> {
                    // event handling comes here
                    log.debug("{} has expired", message);
                },
                new PatternTopic("__keyevent@*__:expired"));
        return listenerContainer;
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
    //         log.debug("RefreshToken with key={}, value={} has expired", key, value);
    //     }
    // }
}
