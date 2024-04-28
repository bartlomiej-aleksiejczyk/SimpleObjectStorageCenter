package com.example.zasobnik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//TODO: Error messages are too informative
@EnableJpaAuditing
@SpringBootApplication
public class ZasobnikApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZasobnikApplication.class, args);
	}

}
