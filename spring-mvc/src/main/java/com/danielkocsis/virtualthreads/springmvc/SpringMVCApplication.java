package com.danielkocsis.virtualthreads.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "com.danielkocsis.virtualthreads")
@ConfigurationPropertiesScan(basePackages = "com.danielkocsis.virtualthreads")
public class SpringMVCApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMVCApplication.class, args);
    }


}
