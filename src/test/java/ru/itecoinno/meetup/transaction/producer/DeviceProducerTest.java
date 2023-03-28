package ru.itecoinno.meetup.transaction.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.itecoinno.meetup.transaction.TestConfiguration;
import ru.itecoinno.meetup.transaction.model.Signal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
@Slf4j
public class DeviceProducerTest {



    @Value("${signal.topic.name}")
    private String topicName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Value("${test.gen.devices}")
    private Integer devices;

    @Value("${test.gen.signals-per-device}")
    private Integer signalPerDevice;

    @Value("${test.gen.time-gen-millsec}")
    private Integer timeGenSec;

    @Value("${test.gen.time-shuffle-enable}")
    private boolean timeShuffleEnable;

    @Value("${test.gen.signal-shuffle-enable}")
    private boolean signalShuffleEnable;

    @Test
    public void singleSignalSource() {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(devices);
        for(int i=0; i<devices; ++i) {
            GenValue genValue = new GenValue(i);
            taskExecutor.execute(genValue);
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    private class GenValue implements Runnable {

        private final int partition;
        public GenValue(int partition) {
            this.partition = partition;
        }

        @Override
        public void run() {
            int delay = timeGenSec/signalPerDevice;
            LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0));
            List<LocalDateTime> dateTimes = new ArrayList<>(signalPerDevice);
            for (long l = 0L; l < signalPerDevice; ++l) {
                dateTimes.add(startTime.minusSeconds(l*delay));
            }
            if (timeShuffleEnable)
                Collections.shuffle(dateTimes);
            try {
                for (int i = 0; i < signalPerDevice; ++i) {
                    int deviceId = partition;
                    if (signalShuffleEnable)
                        deviceId = i%devices;
                    Signal signal = Signal.builder().deviceId(deviceId+1)
                            .deviceName("line" + deviceId).weight(10).ts(dateTimes.get(i)).build();
                    template.send(topicName, partition, signal.getDeviceName(), objectMapper.writeValueAsString(signal));
                    Thread.sleep(delay);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            template.flush();
        }
    }
}
