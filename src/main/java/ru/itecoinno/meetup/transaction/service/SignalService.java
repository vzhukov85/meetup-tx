package ru.itecoinno.meetup.transaction.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.itecoinno.meetup.transaction.entity.DeviceSignal;
import ru.itecoinno.meetup.transaction.model.Signal;
import ru.itecoinno.meetup.transaction.repository.DeviceSignalRepository;
import ru.itecoinno.meetup.transaction.repository.DeviceSignalWithLockRepository;

import java.util.Optional;

@Service
@Slf4j
public class SignalService {
    @Autowired
    private DeviceSignalRepository deviceSignalRepository;

    @Autowired
    private DeviceSignalWithLockRepository deviceSignalWithLockRepository;

    @Value("${signal.topic.lock-enable:true}")
    private Boolean lockEnable;


    @Transactional()
    public void processSignal(Signal deviceSignal) {
        Optional<DeviceSignal> device = null;
        if (lockEnable)
            device = deviceSignalWithLockRepository.findById(deviceSignal.getDeviceId());
        else
            device = deviceSignalRepository.findById(deviceSignal.getDeviceId());
        device.ifPresentOrElse(d -> {
            d.setSignalCount(d.getSignalCount() + 1);
            d.setWeight(d.getWeight() + deviceSignal.getWeight());
            if (d.getLastDateTime() == null || d.getLastDateTime().isBefore(deviceSignal.getTs())) {
                d.setLastDateTime(deviceSignal.getTs());
            }
            deviceSignalRepository.save(d);
        }, () -> {
            log.error("Не найдено устройсво {}", deviceSignal.getDeviceName());
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 6, backoff = @Backoff(delay = 500, multiplier = 2))
    public void processSignalRepeatable(Signal deviceSignal) {
        Optional<DeviceSignal> device = null;
        device = deviceSignalRepository.findById(deviceSignal.getDeviceId());
        device.ifPresentOrElse(d -> {
            d.setSignalCount(d.getSignalCount() + 1);
            d.setWeight(d.getWeight() + deviceSignal.getWeight());
            if (d.getLastDateTime() == null || d.getLastDateTime().isBefore(deviceSignal.getTs())) {
                d.setLastDateTime(deviceSignal.getTs());
            }
            deviceSignalRepository.save(d);
        }, () -> {
            log.error("Не найдено устройсво {}", deviceSignal.getDeviceName());
        });
    }

    @Recover
    public void recovery(Signal deviceSignal) {
        log.warn("Не удалось обработать сигнал {}", deviceSignal.getDeviceId());
    }

}
