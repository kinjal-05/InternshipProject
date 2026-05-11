package com.apigatewayservice.security;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		super(Config.class);
		this.jwtUtil = jwtUtil;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return unauthorized(exchange);
			}

			String token = authHeader.substring(7);

			try {
				long userId = jwtUtil.getUserId(token);
				String role = jwtUtil.getRole(token);

				ServerWebExchange mutatedExchange = exchange.mutate()
						.request(r -> r.headers(headers -> {
							headers.set("X-User-Id", String.valueOf(userId));
							headers.set("X-User-Role", role != null ? role : "USER");
						}))
						.build();

				return chain.filter(mutatedExchange);

			} catch (JwtException e) {
				return unauthorized(exchange);
			}
		};
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	public static class Config {}
}