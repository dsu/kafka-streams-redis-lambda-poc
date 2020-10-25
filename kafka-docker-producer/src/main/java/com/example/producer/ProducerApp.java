package com.example.producer;

import com.example.producer.service.ProductsService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ProducerApp {

    @Autowired
    private ProductsService service;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProducerApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 1000)
    public void startProducer() throws IOException, CsvValidationException {
        service.importCSV();
    }
}
