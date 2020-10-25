package com.example.producer.service;

import com.example.producer.model.ProductData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.Consumer;


@Component
public class KafkaSender {

    protected static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);


    @Value("${spring.kafka.products.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, ProductData> kafkaTemplate;

    public void send(ProductData payload, Consumer<SendResult<String, ProductData>> callback, Runnable callbackOnError) {

        Message<ProductData> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.MESSAGE_KEY, String.valueOf(payload.getProductId()))
                .build();

        logger.debug("kafkaTemplate {}", kafkaTemplate);

        ListenableFuture<SendResult<String, ProductData>> future = kafkaTemplate.send(message);
        future.addCallback(
                new ListenableFutureCallback<SendResult<String, ProductData>>() {

                    @Override
                    public void onSuccess(final SendResult<String, ProductData> message) {
                        logger.info("sent message= " + message.getRecordMetadata() + " with offset= " + message.getRecordMetadata().offset());
                        callback.accept(message);
                    }

                    @Override
                    public void onFailure(final Throwable throwable) {
                        logger.error("unable to send message= ", throwable);
                        callbackOnError.run();
                    }
                });
        logger.info("Message: {} send", payload);
    }


}
