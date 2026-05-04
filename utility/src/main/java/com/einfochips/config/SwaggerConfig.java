package com.einfochips.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
@Configuration
@Profile({ "dev", "qa" })
public class SwaggerConfig {

	// Each service provides its own values via application.properties
	@Value("${swagger.title}")
	private String title;

	@Value("${swagger.description}")
	private String description;

	@Value("${swagger.group}")
	private String group;

	@Value("${swagger.base-package}")
	private String basePackage;

	/**
	 * Creates the global OpenAPI configuration bean.
	 * This defines the API metadata (title, description, version)
	 * and sets up JWT Bearer token authentication for all endpoints.
	 *
	 * @return OpenAPI instance with info and security configuration
	 */
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title(title)
						.description(description)
						.version("1.0"))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new Components()
						.addSecuritySchemes("BearerAuth", new SecurityScheme()
								.name("Authorization")
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}

	/**
	 * Creates a grouped API bean to organize endpoints under a named group.
	 * Each microservice registers its own group using values from application.properties.
	 * This allows multiple services to appear as separate groups in one Swagger UI.
	 *
	 * Example:
	 *   productservice → group = "product-service", basePackage = "com.einfochips.productservice"
	 *   userservice    → group = "user-service",    basePackage = "com.einfochips.userservice"
	 *
	 * @return GroupedOpenApi instance scoped to this service's controllers
	 */
	@Bean
	public GroupedOpenApi groupedOpenApi() {
		return GroupedOpenApi.builder()
				.group(group)
				.packagesToScan(basePackage)
				.pathsToMatch("/**")
				.build();
	}
}