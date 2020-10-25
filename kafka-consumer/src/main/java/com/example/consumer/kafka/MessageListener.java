package com.example.consumer.kafka;

import com.example.consumer.model.ProductData;
import com.example.consumer.redis.PersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final PersistenceService persistenceService;

    @KafkaListener(topics = "${spring.kafka.products.topic}", containerFactory = "subscriptionListenerContainerFactory")
    public void listen(ProductData message) {
        persistenceService.saveProducts(Arrays.asList(message));
        log.info("Saved a new product {}", message.getProductId());
        log.info("Total size: {}", persistenceService.getProducts().size());
    }

}
