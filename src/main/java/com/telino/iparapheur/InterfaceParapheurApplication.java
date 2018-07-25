package com.telino.iparapheur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InterfaceParapheurApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterfaceParapheurApplication.class, args);

	}

}
