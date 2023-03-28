package ru.itecoinno.meetup.transaction.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
}
