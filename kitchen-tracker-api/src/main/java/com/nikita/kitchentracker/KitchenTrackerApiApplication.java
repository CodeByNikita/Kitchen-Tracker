package com.nikita.kitchentracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KitchenTrackerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenTrackerApiApplication.class, args);
	}

}
