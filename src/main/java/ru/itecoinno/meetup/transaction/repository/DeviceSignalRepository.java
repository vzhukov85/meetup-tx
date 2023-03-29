package ru.itecoinno.meetup.transaction.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itecoinno.meetup.transaction.entity.DeviceSignal;

import java.time.LocalDateTime;

@Repository
public interface DeviceSignalRepository extends CrudRepository<DeviceSignal, Integer> {
    @Modifying
    @Query("update DeviceSignal d set d.signalCount = d.signalCount + 1, " +
            "d.weight=d.weight + :weight, " +
            "d.lastDateTime = :ts " +
            "where d.deviceId = :deviceId")
    int updateSignalData(@Param("weight") int weight, @Param("deviceId") int deviceId, @Param("ts") LocalDateTime ts);
}
