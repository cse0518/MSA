package com.humuson.eurekaclient.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class Producer {

    @Value(value = "${topic.name}")
    private String topicName;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                String data = result.getProducerRecord().value();
                long offset = result.getRecordMetadata().offset();
                log.info("Sent message=[" + data + "] with offset=[" + offset + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send message - due to : " + ex.getMessage());
            }
        });
    }
}