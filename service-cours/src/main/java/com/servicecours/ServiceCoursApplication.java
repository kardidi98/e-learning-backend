package com.servicecours;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServiceCoursApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCoursApplication.class, args);
	}

}
