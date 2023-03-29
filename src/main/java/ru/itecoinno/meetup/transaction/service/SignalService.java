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

    public void processSignalFirst(Signal deviceSignal) {
        Optional<DeviceSignal> device = deviceSignalRepository.findById(deviceSignal.getDeviceId());
        device.ifPresentOrElse(d -> {
            d.setSignalCount(d.getSignalCount() + 1);
            d.setWeight(d.getWeight() + deviceSignal.getWeight());
            d.setLastDateTime(deviceSignal.getTs());
            deviceSignalRepository.save(d);
        }, () -> {
            log.error("Не найдено устройсво {}", deviceSignal.getDeviceName());
        });
    }

    @Transactional
    public void processSignalUpdate(Signal deviceSignal) {
        deviceSignalRepository.updateSignalData(deviceSignal.getWeight(), deviceSignal.getDeviceId(), deviceSignal.getTs());
    }

    @Transactional
    public void processSignalLock(Signal deviceSignal) {
        Optional<DeviceSignal> device = deviceSignalWithLockRepository.findById(deviceSignal.getDeviceId());
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
        Optional<DeviceSignal> device = deviceSignalRepository.findById(deviceSignal.getDeviceId());
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

    @Retryable(maxAttempts = 6, backoff = @Backoff(delay = 500, multiplier = 2))
    public void processSignalRepeatableOptimistic(Signal deviceSignal) {
        Optional<DeviceSignal> device = deviceSignalRepository.findById(deviceSignal.getDeviceId());
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
}
