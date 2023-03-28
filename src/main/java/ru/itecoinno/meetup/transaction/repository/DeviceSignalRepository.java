package ru.itecoinno.meetup.transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itecoinno.meetup.transaction.entity.DeviceSignal;

@Repository
public interface DeviceSignalRepository extends CrudRepository<DeviceSignal, Integer> {

}
