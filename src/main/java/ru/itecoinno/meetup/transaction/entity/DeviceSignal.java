package ru.itecoinno.meetup.transaction.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class DeviceSignal {
    @Id
    private Integer deviceId;
    private String deviceName;
    private int signalCount;
    private int weight;
    private LocalDateTime lastDateTime;
    //@Version
    private int version;
}
