package com.eric_eldard.portfolio.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil
{
    private final JwtParser jwtParser;

    private final SecretKey jwtSecretKey;


    public JwtUtil(@Value("${portfolio.security.jwt.signing-key}") String jwtSecretKeyValue)
    {
        jwtSecretKey = new SecretKeySpec(jwtSecretKeyValue.trim().getBytes(), "HmacSHA256");
        this.jwtParser = Jwts.parser().verifyWith(jwtSecretKey).build();
    }


    public String buildToken(String subject, Map<String, String> claims, Date issuedAt, Date expiration)
    {
        return Jwts.builder()
            .claims(Jwts.claims()
                .subject(subject)
                .add(claims)
                .build()
            )
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(jwtSecretKey)
            .compact();
    }

    public Jws<Claims> resolveClaims(String token)
    {
        return jwtParser.parseSignedClaims(token);
    }

    public boolean validate(Jws<Claims> signedToken) throws AuthenticationException
    {
        return signedToken.getPayload().getExpiration().after(new Date());
    }
}