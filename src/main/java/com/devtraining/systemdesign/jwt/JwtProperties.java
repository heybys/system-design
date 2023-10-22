package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.ENC;
import io.jsonwebtoken.Jwts.KEY;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.KeyAlgorithm;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Duration;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String authoritiesKey = "aut";
    private final Duration accessTokenTtl = Duration.ofMinutes(5);
    private final Duration refreshTokenTtl = Duration.ofDays(14);
    private final KeyAlgorithm<PublicKey, PrivateKey> alg = KEY.RSA_OAEP_256;
    private final AeadAlgorithm enc = ENC.A256GCM;

    private final JwtType jwtType;
    private final SecretKey secretKey;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtProperties(String password, JwtType jwtType) {
        this.jwtType = jwtType;

        byte[] keyBytes = Decoders.BASE64.decode(password);
        SecureRandom random = new SecureRandom(keyBytes);
        this.secretKey = SIG.HS256.key().random(random).build();

        KeyPair keyPair = SIG.RS256.keyPair().build();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
}
