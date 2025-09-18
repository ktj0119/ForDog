package com.example.forDog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ForDogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForDogApplication.class, args);

		System.out.println("-----------start--------------");
	}

}
