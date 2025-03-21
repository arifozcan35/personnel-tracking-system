package com.personneltrackingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonelTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonelTrackingSystemApplication.class, args);
	}

	/*
	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String description,
								 @Value("${application-version}") String version){
		return new OpenAPI()
				.info(new Info()
						.title("PTS API")
				.version(version)
				.description(description)
				.license(new License().name("PTS API License")));
	}

	 */

}
