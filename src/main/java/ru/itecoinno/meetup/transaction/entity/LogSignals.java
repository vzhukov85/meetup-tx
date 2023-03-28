package ru.itecoinno.meetup.transaction.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class LogSignals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer deviceId;
    private String deviceName;
    private int signalCount;
    private int weight;
    private LocalDateTime lastDateTime;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime created;
}
