package com.einfochips.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();

		// Allow productservice to call userservice
		config.addAllowedOrigin("http://localhost:8080");
		config.addAllowedOrigin("http://localhost:8081");

		// Allow all headers and methods
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// Apply CORS only to swagger api-docs endpoint
		source.registerCorsConfiguration("/v3/api-docs/**", config);

		return new CorsFilter(source);
	}
}