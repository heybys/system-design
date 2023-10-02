package com.devtraining.systemdesign.member.domain;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String key;

    private String value;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long ttl;

    private OffsetDateTime createdAt;

    @Builder
    public RefreshToken(String key, String value, Duration ttl) {
        this.key = key;
        this.value = value;
        this.ttl = TimeUnit.MILLISECONDS.convert(ttl);
        this.createdAt = OffsetDateTime.now();
    }
}
