package com.devtraining.systemdesign.jwt;

import io.jsonwebtoken.Jwts.ENC;
import io.jsonwebtoken.Jwts.KEY;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.AeadAlgorithm;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.KeyAlgorithm;
import io.jsonwebtoken.security.RsaPrivateJwk;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
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
    private final RsaPrivateJwk privateJwk;

    public JwtProperties(String password, JwtType jwtType) {
        this.jwtType = jwtType;

        byte[] keyBytes = Decoders.BASE64.decode(password);
        SecureRandom random = new SecureRandom(keyBytes);

        KeyPair keyPair = SIG.RS256.keyPair().random(random).build();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        this.privateJwk = Jwks.builder().key(privateKey).idFromThumbprint().build();

        RSAPublicKey pubKeyFromPriKey = privateJwk.toPublicJwk().toKey();
        assert publicKey.equals(pubKeyFromPriKey);
    }
}
