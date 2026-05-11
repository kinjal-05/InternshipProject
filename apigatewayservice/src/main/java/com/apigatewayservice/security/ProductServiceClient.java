package com.apigatewayservice.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Calls ProductService internal APIs to check ownership.
 * These endpoints should be secured (e.g. internal-only or service token).
 */
@Slf4j
@Component
public class ProductServiceClient {

	private final WebClient webClient;

	public ProductServiceClient(@Value("${services.product-service.url}") String productServiceUrl) {
		this.webClient = WebClient.builder()
				.baseUrl(productServiceUrl)
				.build();
	}

	/**
	 * Check if given userId is the owner of the category that contains this product.
	 *
	 * @param productId the product being accessed
	 * @param userId    the requesting user
	 * @return true if the user owns the category
	 */
	public Mono<Boolean> isUserOwnerOfProductCategory(long productId, long userId) {
		return webClient.get()
				.uri("/internal/products/{productId}/owner-check?userId={userId}", productId, userId)
				.retrieve()
				.bodyToMono(Boolean.class)
				.doOnError(e -> log.error("Error checking product ownership: {}", e.getMessage()))
				.onErrorReturn(false);
	}

	/**
	 * Check if given userId is the owner of this category.
	 *
	 * @param categoryId the category being accessed
	 * @param userId     the requesting user
	 * @return true if the user owns the category
	 */
	public Mono<Boolean> isUserOwnerOfCategory(long categoryId, long userId) {
		return webClient.get()
				.uri("/internal/categories/{categoryId}/owner-check?userId={userId}", categoryId, userId)
				.retrieve()
				.bodyToMono(Boolean.class)
				.doOnError(e -> log.error("Error checking category ownership: {}", e.getMessage()))
				.onErrorReturn(false);
	}
}
