package com.onedigit.utah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//TODO: to fix frontend
//TODO: delete time measures
//TODO: add more exchanges
@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
public class UtahApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtahApplication.class, args);
	}
}
