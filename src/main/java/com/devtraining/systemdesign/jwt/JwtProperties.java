package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String authoritiesKey = "aut";
    private final Duration accessTokenTtl = Duration.ofMinutes(10);
    private final Duration refreshTokenTtl = Duration.ofDays(14);
    private final Key key;

    public JwtProperties(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        // this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}
