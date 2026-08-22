package com.williamhsieh.financeapp.service.auth;

import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.williamhsieh.financeapp.config.JwtProperties;
import com.williamhsieh.financeapp.entity.user.User;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtService(
        JwtEncoder jwtEncoder,
        JwtProperties jwtProperties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public AccessTokenResult generateAccessToken(
        User user,
        String sessionId
    ) {
        Instant issuedAt = Instant.now();

        long expirationSeconds =
            jwtProperties.accessTokenExpirationSeconds();

        Instant expiresAt = issuedAt.plusSeconds(
            expirationSeconds
        );

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .type("JWT")
            .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.issuer())
            .subject(user.getId().toString())
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("userNumber", user.getUserNumber())
            .claim("email", user.getEmail())
            .claim("sid", sessionId)
            .claim("tokenType", "access")
            .build();

        String token = jwtEncoder
            .encode(
                JwtEncoderParameters.from(
                    header,
                    claims
                )
            )
            .getTokenValue();

        return new AccessTokenResult(
            token,
            expirationSeconds
        );
    }
}