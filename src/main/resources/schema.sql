CREATE TABLE IF NOT EXISTS device_signal
(
    device_id        SERIAL NOT NULL,
    device_name      varchar NOT NULL,
    signal_count     int8 NOT NULL DEFAULT 0,
    weight           int8 NOT NULL DEFAULT 0,
    last_date_time   timestamp NULL,
    version          int8 NOT NULL DEFAULT 1,

    CONSTRAINT device_id_pk PRIMARY KEY (device_id)
);