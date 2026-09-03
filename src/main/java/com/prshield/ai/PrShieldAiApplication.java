package com.prshield.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PrShieldAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrShieldAiApplication.class, args);
    }
}