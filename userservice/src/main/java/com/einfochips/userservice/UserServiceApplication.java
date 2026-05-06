package com.einfochips.userservice;

import com.einfochips.utility.config.CorsConfig;
import com.einfochips.utility.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CorsConfig.class, SwaggerConfig.class})

public class UserServiceApplication {
	public static void main(String[] args)
	{
		SpringApplication.run(UserServiceApplication.class,args);
	}
}
