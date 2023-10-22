package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Slf4j
public class JwtDecoder {
    private final JwtType jwtType;
    private final String authoritiesKey;
    private final JwtParser jwtParser;

    public JwtDecoder(JwtProperties properties) {
        this.jwtType = properties.getJwtType();
        this.authoritiesKey = properties.getAuthoritiesKey();

        if (JwtType.SIG.equals(this.jwtType)) {
            // SecretKey secretKey = properties.getSecretKey();
            // this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
            PublicKey publicKey = properties.getPublicKey();
            this.jwtParser = Jwts.parser().verifyWith(publicKey).build();
        } else {
            PrivateKey privateKey = properties.getPrivateKey();
            this.jwtParser = Jwts.parser().decryptWith(privateKey).build();
        }
    }

    public boolean isExpired(String token) {
        try {
            jwtParser.parse(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public UserDetails getUserDetails(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        List<String> authorityNames = (List<String>) claims.get(this.authoritiesKey);

        Assert.notNull(authorityNames, "authorityNames from claims is null");
        Set<SimpleGrantedAuthority> authorities =
                authorityNames.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return User.builder()
                .username(username)
                .password("")
                .authorities(authorities)
                .build();
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        if (JwtType.SIG.equals(jwtType)) {
            Jws<Claims> jws = jwtParser.parseSignedClaims(token);
            return jws.getPayload();
        } else {
            Jwe<Claims> jwe = jwtParser.parseEncryptedClaims(token);
            return jwe.getPayload();
        }
    }
}
