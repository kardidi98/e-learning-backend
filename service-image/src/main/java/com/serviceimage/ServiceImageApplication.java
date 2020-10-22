package com.serviceimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServiceImageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceImageApplication.class, args);
	}

}
