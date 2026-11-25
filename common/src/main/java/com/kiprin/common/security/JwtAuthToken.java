package com.kiprin.common.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import com.kiprin.common.auth.AuthPrincipal;

/**
 * Authenticated request: principal is an {@link AuthPrincipal}, the verified {@link Jwt} stays available via
 * {@link #getToken()}. Unlike {@code UsernamePasswordAuthenticationToken} it is not wiped after authentication,
 * so the raw token can be forwarded to downstream services.
 */
public class JwtAuthToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    public JwtAuthToken(
            Jwt jwt,
            AuthPrincipal principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(jwt, principal, jwt, authorities);
        setAuthenticated(true);
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return getToken().getClaims();
    }
}
