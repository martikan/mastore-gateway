package org.martikan.mastore.apigateway.utils;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util class for Jwt actions.
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${token.expiration}")
    private String expirationTime;

    @Value("${token.secret}")
    private String secret;

    public boolean isTokenValid(final String jwtToken) {

        var valid = false;

        try {

            final var subject = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();

            if (subject != null && !subject.isBlank()) {
                valid = true;
            }

        } catch (Exception e) {
            log.warn("JWT Token validation error", e);
        }

        return valid;
    }

}
