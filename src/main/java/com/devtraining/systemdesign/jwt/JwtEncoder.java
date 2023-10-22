package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.KeyAlgorithm;
import io.jsonwebtoken.security.RsaPrivateJwk;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class JwtEncoder {
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final String authoritiesKey;
    private final JwtType jwtType;
    private final RsaPrivateJwk privateJwk;
    private final KeyAlgorithm<PublicKey, PrivateKey> alg;
    private final AeadAlgorithm enc;

    public JwtEncoder(JwtProperties properties) {
        this.accessTokenTtl = properties.getAccessTokenTtl();
        this.refreshTokenTtl = properties.getRefreshTokenTtl();
        this.authoritiesKey = properties.getAuthoritiesKey();
        this.jwtType = properties.getJwtType();
        this.privateJwk = properties.getPrivateJwk();
        this.alg = properties.getAlg();
        this.enc = properties.getEnc();
    }

    public String createAccessToken(String username, Set<SimpleGrantedAuthority> authorities) {
        return createToken(username, authorities, this.accessTokenTtl);
    }

    public String createRefreshToken(String username, Set<SimpleGrantedAuthority> authorities) {
        return createToken(username, authorities, this.refreshTokenTtl);
    }

    private String createToken(String username, Set<SimpleGrantedAuthority> authorities, Duration ttl) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add(
                        this.authoritiesKey,
                        authorities.stream()
                                .map(SimpleGrantedAuthority::getAuthority)
                                .toList())
                .build();

        Date now = new Date();
        Duration expiration = Duration.ofMillis(now.getTime()).plus(ttl);

        JwtBuilder jwtBuilder = Jwts.builder().claims(claims).issuedAt(now).expiration(new Date(expiration.toMillis()));

        if (JwtType.SIG.equals(this.jwtType)) {
            return jwtBuilder.signWith(getPrivateKey(), SIG.RS256).compact();
        } else {
            return jwtBuilder.encryptWith(getPublicKey(), this.alg, this.enc).compact();
        }
    }

    private RSAPublicKey getPublicKey() {
        return this.privateJwk.toPublicJwk().toKey();
    }

    private RSAPrivateKey getPrivateKey() {
        return this.privateJwk.toKey();
    }
}
