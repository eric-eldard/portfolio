package com.eric_eldard.portfolio.model.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.eric_eldard.portfolio.util.Constants;

/**
 * An {@link Authentication} which holds {@link Jws<Claims>} as the principal and provides convenience methods for
 * accessing specific claims.
 */
public class JwsAuthToken extends AbstractAuthenticationToken
{
    private final Jws<Claims> jwsClaims;

    public JwsAuthToken(Jws<Claims> jwsClaims) {
        super(makeGrantAuthorities(jwsClaims));
        this.jwsClaims = jwsClaims;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials()
    {
        return null;
    }

    @Override
    public Jws<Claims> getPrincipal()
    {
        return jwsClaims;
    }

    public long userId()
    {
        return Long.parseLong(jwsClaims.getPayload().getSubject());
    }

    public String username()
    {
        return jwsClaims.getPayload().get("username", String.class);
    }

    public Date issuedAt()
    {
        return jwsClaims.getPayload().getIssuedAt();
    }

    public Date serverStart()
    {
        return jwsClaims.getPayload().get(Constants.SERVER_START_CLAIM, Date.class);
    }

    public boolean isExpired() throws AuthenticationException
    {
        return jwsClaims.getPayload().getExpiration().before(new Date());
    }

    public int getSecondsUntilExpiration()
    {
        return (int) ChronoUnit.SECONDS.between(
            new Date().toInstant(),
            jwsClaims.getPayload().getExpiration().toInstant()
        );
    }

    public boolean accountLocked()
    {
        return jwsClaims.getPayload().get("locked_on", Date.class) != null;
    }

    public boolean accountDisabled()
    {
        return !jwsClaims.getPayload().get("enabled", Boolean.class);
    }

    public boolean accountExpired()
    {
        Date authorizedUntil = jwsClaims.getPayload().get("authorized_until", Date.class);
        return authorizedUntil != null && authorizedUntil.before(new Date());
    }

    private static Collection<GrantedAuthority> makeGrantAuthorities(Jws<Claims> jwsClaims)
    {
        return jwsClaims.getPayload().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("GrantedAuthority-"))
            .map(Map.Entry::getValue)
            .map(Object::toString)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }
}