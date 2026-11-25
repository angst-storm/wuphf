package com.kiprin.common.security;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.JwtService;
import com.kiprin.common.auth.RoleName;

/**
 * Turns a verified {@link Jwt} into an authentication whose principal is an {@link AuthPrincipal}
 * and whose authorities are {@code ROLE_<name>}. The raw {@link Jwt} stays available via the token.
 */
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        AuthPrincipal principal;
        try {
            UUID userId = UUID.fromString(jwt.getSubject());
            principal = new AuthPrincipal(userId, parseRoles(jwt.getClaimAsStringList(JwtService.CLAIM_ROLES)));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidBearerTokenException("Invalid token claims", e);
        }

        List<GrantedAuthority> authorities = principal.roles().stream()
                .sorted()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
        return new JwtAuthToken(jwt, principal, authorities);
    }

    private static Set<RoleName> parseRoles(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            return Set.of();
        }
        EnumSet<RoleName> result = EnumSet.noneOf(RoleName.class);
        for (String item : raw) {
            RoleName role = RoleName.from(item);
            if (role != null) {
                result.add(role);
            }
        }
        return result;
    }
}
