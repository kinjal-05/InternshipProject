package com.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.utility"
})
public class APIGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(APIGatewayServiceApplication.class, args);
	}
}
