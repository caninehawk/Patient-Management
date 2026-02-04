package com.example.analytics_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics="patient", groupId="analytics-service")
    public void consumeEvent(byte[] event) throws InvalidProtocolBufferException {
        try {
            System.out.println("Received: " + new String(event));
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            //business logic for analytics can be added here

            log.info("Received Patient Event with name: {}", patientEvent.getName());

        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }
}
