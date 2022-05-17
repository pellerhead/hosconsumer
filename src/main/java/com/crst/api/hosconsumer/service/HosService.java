package com.crst.api.hosconsumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

@Service
public class HosService {
    Logger logger = LoggerFactory.getLogger(HosService.class);

    @Value("${kafka.logs.topic}")
    private String logsTopic;

    @Value("${kafka.timers.topic}")
    private String timersTopic;

    @Value("${kafka.servers}")
    private String kafkaServers;

    @Value("${partition}")
    private int partition;

    @Value("${replication}")
    private int replication;

    @Value("${groupId}")
    private String hosGroupId;

    @Value("${autoOffset}")
    private String autoOffset;

    public void process() {
        logger.info("HosService.process()");

        long startTime = System.currentTimeMillis();

        Properties consumerProperties = new Properties();

        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, hosGroupId);
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffset);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);

        final Thread consumerThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                consumer.wakeup();

                try {
                    consumerThread.join();
                } catch (InterruptedException e) {
                    logger.error("Interrupted while waiting for consumer thread to exit", e);
                }
            }
        });

        try {

            consumer.subscribe(Arrays.asList(logsTopic));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Received message: (" + record.key() + ", " + record.value() + ") at offset "
                            + record.offset());
                }
            }
        } catch (WakeupException e) {
            logger.error("Consumer thread interrupted", e);
        } catch (Exception e) {
            logger.error("Error while consuming", e);
        } finally {
            consumer.close();
        }

        logger.debug("HosService.process() - Took {} ms", System.currentTimeMillis() - startTime);

    }
}