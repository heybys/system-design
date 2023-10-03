package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class JwtEncoder {
    private final JwtProperties properties;

    public JwtEncoder(JwtProperties properties) {
        this.properties = properties;
    }

    public String createAccessToken(String username, Set<SimpleGrantedAuthority> authorities) {
        return createToken(username, authorities, properties.getAccessTokenTtl());
    }

    public String createRefreshToken(String username, Set<SimpleGrantedAuthority> authorities) {
        return createToken(username, authorities, properties.getRefreshTokenTtl());
    }

    private String createToken(String username, Set<SimpleGrantedAuthority> authorities, Duration ttl) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(
                properties.getAuthoritiesKey(),
                authorities.stream().map(SimpleGrantedAuthority::getAuthority).toList());

        Date now = new Date();
        Duration expiration = Duration.ofMillis(now.getTime()).plus(ttl);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(expiration.toMillis()))
                .signWith(properties.getKey())
                // .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Duration getRefreshTokenTtl() {
        return properties.getRefreshTokenTtl();
    }
}
