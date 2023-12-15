package com.danielkocsis.virtualthreadstomcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VirtualThreadsTomcatApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualThreadsTomcatApplication.class, args);
	}

}
