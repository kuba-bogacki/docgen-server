package com.gateway.filter;

import com.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayAuthenticationFilter extends AbstractGatewayFilterFactory<GatewayAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public GatewayAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Brak nagłówka Authorization");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            // 2. Walidacja tokena (lokalnie, matematycznie!)
            try {
                jwtUtil.isInvalid(authHeader);

                // Opcjonalnie: Wyciągnij ID usera z tokena i dodaj do nagłówka,
                // aby company-service wiedziało, kto wykonuje zapytanie.
                String userId = jwtUtil.extractUserId(authHeader);
                exchange = exchange.mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();

            } catch (Exception e) {
                return onError(exchange, "Nieprawidłowy lub wygasły token");
            }

            // 3. Jeśli wszystko OK, puść żądanie dalej
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Pusta klasa konfiguracyjna wymagana przez Spring Cloud Gateway
    }
}
