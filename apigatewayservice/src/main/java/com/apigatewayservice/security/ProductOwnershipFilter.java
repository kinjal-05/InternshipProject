package com.apigatewayservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use in application.yml as:
 * - ProductOwnershipFilter
 *
 * No yml change required.
 *
 * Handles:
 * GET  /api/v1/products/{productId}
 * GET  /api/v1/products?categoryId=1
 * POST /api/v1/products
 * PUT  /api/v1/products/{id}
 */
@Slf4j
@Component
public class ProductOwnershipFilter
		extends AbstractGatewayFilterFactory<ProductOwnershipFilter.Config> {

	private final ProductServiceClient productServiceClient;

	private static final Pattern PRODUCT_ID_PATTERN =
			Pattern.compile("^/api/v1/products/(\\d+)(/.*)?$");

	/**
	 * Constructor Injection
	 */
	public ProductOwnershipFilter(ProductServiceClient productServiceClient) {
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

			/**
			 * ADMIN bypass
			 */
			if ("ADMIN".equalsIgnoreCase(role)) {
				return chain.filter(exchange);
			}

			String path = exchange.getRequest()
					.getPath()
					.value();

			String method = exchange.getRequest()
					.getMethod()
					.name();

			/**
			 * CASE 1:
			 * /api/v1/products/{productId}
			 */
			Matcher matcher = PRODUCT_ID_PATTERN.matcher(path);

			if (matcher.matches()) {

				long productId =
						Long.parseLong(matcher.group(1));

				return productServiceClient
						.isUserOwnerOfProductCategory(productId, userId)
						.flatMap(isOwner -> {

							if (Boolean.TRUE.equals(isOwner)) {
								return chain.filter(exchange);
							}

							log.warn(
									"User {} is NOT owner of product {} category",
									userId,
									productId
							);

							return forbidden(
									exchange,
									"You do not own this product category"
							);
						});
			}

			/**
			 * CASE 2:
			 * /api/v1/products?categoryId=1
			 */
			List<String> categoryIds =
					exchange.getRequest()
							.getQueryParams()
							.get("categoryId");

			if (categoryIds != null && !categoryIds.isEmpty()) {

				long categoryId =
						Long.parseLong(categoryIds.get(0));

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
			}

			/**
			 * CASE 3:
			 * POST / PUT allowed
			 * downstream service validates X-User-Id
			 */
			if ("POST".equalsIgnoreCase(method)
					|| "PUT".equalsIgnoreCase(method)) {

				return chain.filter(exchange);
			}

			/**
			 * Default allow
			 */
			return chain.filter(exchange);
		};
	}

	private Mono<Void> forbidden(
			ServerWebExchange exchange,
			String reason
	) {

		log.warn("Access denied: {}", reason);

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