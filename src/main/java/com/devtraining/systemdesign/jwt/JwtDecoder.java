package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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
    private final JwtProperties properties;
    private final JwtParser jwtParser;

    public JwtDecoder(JwtProperties properties) {
        this.properties = properties;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(properties.getKey()).build();
    }

    public boolean isExpired(String token) {
        try {
            jwtParser.parseClaimsJws(token);
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
        List<String> authorityNames = (List<String>) claims.get(properties.getAuthoritiesKey());

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
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        return jws.getBody();
    }
}
