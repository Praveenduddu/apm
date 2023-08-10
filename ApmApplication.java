package de.zeroco.apm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class ApmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApmApplication.class, args);
	}

}
