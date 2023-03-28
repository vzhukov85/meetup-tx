package ru.itecoinno.meetup.transaction.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class Signal {
    private int deviceId;
    private String deviceName;
    private int weight;
    private LocalDateTime ts;
}
