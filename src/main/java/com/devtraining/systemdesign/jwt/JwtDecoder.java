package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.RsaPrivateJwk;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Slf4j
public class JwtDecoder {
    private final String authoritiesKey;
    private final JwtType jwtType;
    private final RsaPrivateJwk privateJwk;

    public JwtDecoder(JwtProperties properties) {
        this.jwtType = properties.getJwtType();
        this.authoritiesKey = properties.getAuthoritiesKey();
        this.privateJwk = properties.getPrivateJwk();
    }

    public boolean isExpired(String token) {
        try {
            getJwtParser().parse(token);
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
            return getJwtParser().parseSignedClaims(token).getPayload();
        } else {
            return getJwtParser().parseEncryptedClaims(token).getPayload();
        }
    }

    private JwtParser getJwtParser() {
        if (JwtType.SIG.equals(this.jwtType)) {
            RSAPublicKey publicKey = this.privateJwk.toPublicJwk().toKey();
            return Jwts.parser().verifyWith(publicKey).build();
        } else {
            RSAPrivateKey privateKey = this.privateJwk.toKey();
            return Jwts.parser().decryptWith(privateKey).build();
        }
    }
}
