package com.utility.config;

import com.utility.dtos.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "dev"})
@EnableConfigurationProperties(SwaggerProperties.class)
@RequiredArgsConstructor
public class SwaggerConfig {

	private final SwaggerProperties props;

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title(props.title())
						.description(props.description())
						.version("1.0"))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(new Components()
						.addSecuritySchemes("BearerAuth",
								new SecurityScheme()
										.name("Authorization")
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")));
	}

	@Bean
	public OperationCustomizer globalHeaderCustomizer() {
		return (operation, handlerMethod) -> {
			operation.addParametersItem(
					new Parameter()
							.in("header")
							.name("Accept-Language")
							.description("Language code (en, es, fr)")
							.required(false)
			);
			return operation;
		};
	}

	@Bean
	public GroupedOpenApi serviceApi() {
		return GroupedOpenApi.builder()
				.group(props.group())                  // e.g. user-service / product-service
				.packagesToScan(props.basePackage())  // service-specific package
				.build();
	}



}