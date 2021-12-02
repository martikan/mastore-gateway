package org.martikan.mastore.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    @Bean
    public GlobalFilter defaultPreFilter() {
        return (exchange, chain) -> {

            log.debug("Pre-filter has been executed.");

            final var requestPath = exchange.getRequest().getPath().toString();
            log.debug("Request path: " + requestPath);


            final var headers = exchange.getRequest().getHeaders();
            headers.keySet().forEach(headerName -> log.debug(headerName + ": " + headers.getFirst(headerName)));

            return chain.filter(exchange);
        };
    }

    @Bean
    public GlobalFilter defaultPostFilter() {

        return (exchange, chain) -> {

            log.debug("Post-filter is being executed.");

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.debug("Post-filter was executed.");
            }));

        };

    }

}
