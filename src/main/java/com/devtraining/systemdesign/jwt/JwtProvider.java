package com.devtraining.systemdesign.jwt;

import com.devtraining.systemdesign.member.domain.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

@Slf4j
public class JwtProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private final Key key;
    private final JwtParser jwtParser;

    public JwtProvider(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        // this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createAccessToken(String username, Set<Authority> authorities, Duration ttl) {
        return createToken(username, authorities, ttl);
    }

    public String createRefreshToken(String username, Set<Authority> authorities, Duration ttl) {
        return createToken(username, authorities, ttl);
    }

    public boolean isValid(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
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

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private Claims getClaims(String token) {
        try {
            Jws<Claims> jws = jwtParser.parseClaimsJws(token);
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to authenticate since the access token is not valid");
            throw new BadCredentialsException("Bad credentials");
        }
    }

    private String createToken(String username, Set<Authority> authorities, Duration ttl) {
        Claims claims = Jwts.claims().setSubject(username);

        claims.put(AUTHORITIES_KEY, authorities.stream().map(Authority::getName).collect(Collectors.joining(",")));

        Date now = new Date();
        Duration expiration = Duration.ofMillis(now.getTime()).plus(ttl);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(expiration.toMillis()))
                .signWith(key)
                // .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
