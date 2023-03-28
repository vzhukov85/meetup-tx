package ru.itecoinno.meetup.transaction.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itecoinno.meetup.transaction.entity.DeviceSignal;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface DeviceSignalWithLockRepository extends CrudRepository<DeviceSignal, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<DeviceSignal> findById(Integer id);
}
