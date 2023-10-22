package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.KeyAlgorithm;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class JwtEncoder {
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final String authoritiesKey;
    private final JwtType jwtType;
    // private final SecretKey secretKey;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final KeyAlgorithm<PublicKey, PrivateKey> alg;
    private final AeadAlgorithm enc;

    public JwtEncoder(JwtProperties properties) {
        this.accessTokenTtl = properties.getAccessTokenTtl();
        this.refreshTokenTtl = properties.getRefreshTokenTtl();
        this.authoritiesKey = properties.getAuthoritiesKey();
        this.jwtType = properties.getJwtType();
        // this.secretKey = properties.getSecretKey();
        this.publicKey = properties.getPublicKey();
        this.privateKey = properties.getPrivateKey();
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
            // return jwtBuilder.signWith(this.secretKey).compact();
            return jwtBuilder.signWith(this.privateKey).compact();
        } else {
            return jwtBuilder.encryptWith(this.publicKey, this.alg, this.enc).compact();
        }
    }
}
