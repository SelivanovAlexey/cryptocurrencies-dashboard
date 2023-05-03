package com.onedigit.utah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UtahApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtahApplication.class, args);
	}

}
