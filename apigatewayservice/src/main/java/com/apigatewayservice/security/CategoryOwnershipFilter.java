package com.apigatewayservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use in application.yml as:
 * - CategoryOwnershipFilter
 *
 * No yml change required.
 */
@Slf4j
@Component
public class CategoryOwnershipFilter
		extends AbstractGatewayFilterFactory<CategoryOwnershipFilter.Config> {

	private final ProductServiceClient productServiceClient;

	private static final Pattern CATEGORY_ID_PATTERN =
			Pattern.compile("^/api/v1/categories/(\\d+)(/.*)?$");

	/**
	 * Constructor Injection
	 */
	public CategoryOwnershipFilter(ProductServiceClient productServiceClient) {
		super(Config.class);
		this.productServiceClient = productServiceClient;
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {

			String userIdHeader = exchange.getRequest()
					.getHeaders()
					.getFirst("X-User-Id");

			String role = exchange.getRequest()
					.getHeaders()
					.getFirst("X-User-Role");

			if (userIdHeader == null) {
				return forbidden(exchange, "Missing user identity");
			}

			long userId;

			try {
				userId = Long.parseLong(userIdHeader);
			} catch (Exception e) {
				return forbidden(exchange, "Invalid user identity");
			}

			String method = exchange.getRequest()
					.getMethod()
					.name();

			String path = exchange.getRequest()
					.getPath()
					.value();

			/**
			 * ADMIN bypass
			 */
			if ("ADMIN".equalsIgnoreCase(role)) {
				return chain.filter(exchange);
			}

			/**
			 * POST -> Create category
			 */
			if ("POST".equalsIgnoreCase(method)) {
				return chain.filter(exchange);
			}

			/**
			 * GET all categories list
			 */
			if ("/api/v1/categories".equals(path)) {
				return chain.filter(exchange);
			}

			/**
			 * Extract category id from path
			 */
			Matcher matcher = CATEGORY_ID_PATTERN.matcher(path);

			if (!matcher.matches()) {
				return chain.filter(exchange);
			}

			long categoryId = Long.parseLong(matcher.group(1));

			/**
			 * Ownership Check
			 */
			return productServiceClient
					.isUserOwnerOfCategory(categoryId, userId)
					.flatMap(isOwner -> {

						if (Boolean.TRUE.equals(isOwner)) {
							return chain.filter(exchange);
						}

						log.warn(
								"User {} is NOT owner of category {}",
								userId,
								categoryId
						);

						return forbidden(
								exchange,
								"You do not own this category"
						);
					});
		};
	}

	private Mono<Void> forbidden(
			ServerWebExchange exchange,
			String reason
	) {
		exchange.getResponse()
				.setStatusCode(HttpStatus.FORBIDDEN);

		exchange.getResponse()
				.getHeaders()
				.add("X-Denied-Reason", reason);

		return exchange.getResponse().setComplete();
	}

	public static class Config {
	}
}