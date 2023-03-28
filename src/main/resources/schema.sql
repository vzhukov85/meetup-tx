CREATE TABLE IF NOT EXISTS device_signal
(
    device_id      SERIAL NOT NULL,
    device_name    varchar NOT NULL,
    signal_count   int8 NOT NULL DEFAULT 0,
    weight         int8 NOT NULL DEFAULT 0,
    last_date_time timestamp NULL,

    CONSTRAINT device_id_pk PRIMARY KEY (device_id)
);


CREATE TABLE IF NOT EXISTS log_signal
(
    id             SERIAL NOT NULL,
    device_id      SERIAL NOT NULL,
    device_name    varchar NOT NULL,
    weight         int8 NOT NULL DEFAULT 0,
    last_date_time timestamp NULL,
    created        timestamp NOT NULL,

    CONSTRAINT log_signal_pk PRIMARY KEY (id)
);