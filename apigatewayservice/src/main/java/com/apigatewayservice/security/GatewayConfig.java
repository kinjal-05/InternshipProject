package com.apigatewayservice.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ProductOwnershipFilter productOwnershipFilter;
	private final CategoryOwnershipFilter categoryOwnershipFilter;

	@Value("${services.product-service.url}")
	private String productServiceUrl;

	@Value("${services.user-service.url}")
	private String userServiceUrl;

	public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
	                     ProductOwnershipFilter productOwnershipFilter,
	                     CategoryOwnershipFilter categoryOwnershipFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.productOwnershipFilter = productOwnershipFilter;
		this.categoryOwnershipFilter = categoryOwnershipFilter;
	}

	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder) {
		return builder.routes()


				.route("user-service-public", r -> r
						.path("/api/v1/users/login", "/api/v1/users/register")
						.uri(userServiceUrl)
				)


				.route("user-service-protected", r -> r
						.path("/api/v1/users/**")
						.filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
						.uri(userServiceUrl)
				)


				.route("product-service-categories", r -> r
						.path("/api/v1/categories/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
								.filter(categoryOwnershipFilter.apply(new CategoryOwnershipFilter.Config()))
						)
						.uri(productServiceUrl)
				)


				.route("product-service-products", r -> r
						.path("/api/v1/products/**")
						.filters(f -> f
								.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
								.filter(productOwnershipFilter.apply(new ProductOwnershipFilter.Config()))
						)
						.uri(productServiceUrl)
				)

				.build();
	}
}