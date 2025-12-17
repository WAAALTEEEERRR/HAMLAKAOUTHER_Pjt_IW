package com.eventmanagement.registrationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RegistrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationServiceApplication.class, args);
	}

	/**
     * Bean RestTemplate pour communiquer avec les autres microservices
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
