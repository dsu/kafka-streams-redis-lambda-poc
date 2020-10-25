package com.example.consumer.kafka;

import com.example.consumer.conf.KafkaConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;

public class KafkaErrorHandler implements ErrorHandler {

    protected static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Override
    public void handle(Exception e, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
        skipError(e, consumer);

    }

    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord) {

    }

    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> consumer) {
        skipError(e, consumer);
    }

    private void skipError(Exception e, Consumer<?, ?> consumer) {
        logger.warn("Kafka  error", e);

        String s = e.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
        String topics = s.split("-")[0];
        int offset = Integer.valueOf(s.split("offset ")[1]);
        int partition = Integer.valueOf(s.split("-")[1].split(" at")[0]);
        TopicPartition topicPartition = new TopicPartition(topics, partition);
        logger.info("Skipping " + partition + " offset " + offset);
        consumer.seek(topicPartition, offset + 1);
    }


}