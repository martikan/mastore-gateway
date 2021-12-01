package org.martikan.mastore.apigateway.filter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.martikan.mastore.apigateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtils jwtUtils;

    @Data
    public static class Config {
        // config props.
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            final var request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing authorization information");
            }

            final var authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);

            final var parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                return onError(exchange, "Incorrect authorization");
            }

            final var jwtToken = parts[1];
            log.debug("FULL TOKEN: " + authHeader);
            log.debug("TOKEN: " + jwtToken);

            if (!jwtUtils.isTokenValid(jwtToken)) {
                return onError(exchange, "JWT token is not valid");
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(final ServerWebExchange exchange, final String err) {

        final var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }

}
