package com.einfochips.utility.dtos;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "swagger")
public record SwaggerProperties(
		String title,
		String description,
		String group,
		String basePackage
) {
}
