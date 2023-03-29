package ru.itecoinno.meetup.transaction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.itecoinno.meetup.transaction.model.Signal;
import ru.itecoinno.meetup.transaction.service.SignalService;

@Service
@Slf4j
public class SignalConsumer {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SignalService signalService;

    @KafkaListener(id="signalData",
            topics="${signal.topic.name}",
            concurrency = "${signal.topic.concurrency}",
            autoStartup = "true",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "signalsFactory")
    public void accept(@Payload ConsumerRecord<String, String> signalData) throws Exception {
        Signal signal = objectMapper.readValue(signalData.value(), Signal.class);
        signalService.processSignalFirst(signal);
        //signalService.processSignalUpdate(signal);
        //signalService.processSignalRepeatable(signal);
        //signalService.processSignalLock(signal);
        //signalService.processSignalRepeatableOptimistic(signal);
    }
}

