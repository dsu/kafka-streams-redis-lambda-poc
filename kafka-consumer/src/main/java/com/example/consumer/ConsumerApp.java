package com.example.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApp {

    protected static final Logger logger = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsumerApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

}
