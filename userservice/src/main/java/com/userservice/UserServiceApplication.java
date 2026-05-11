package com.userservice;

import com.utility.config.CorsConfig;
import com.utility.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {
		"com.userservice",
		"com.utility"
})
@Import({CorsConfig.class, SwaggerConfig.class})

public class UserServiceApplication {
	public static void main(String[] args)
	{
		SpringApplication.run(UserServiceApplication.class,args);
	}
}
