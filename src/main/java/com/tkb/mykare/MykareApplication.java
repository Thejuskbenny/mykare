package com.tkb.mykare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MykareApplication {

	public static void main(String[] args) {
		SpringApplication.run(MykareApplication.class, args);
	}

}
