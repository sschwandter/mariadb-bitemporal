-- See https://mariadb.com/kb/en/application-time-periods/

--
CREATE TABLE t1(
                   name VARCHAR(50),
                   date_1 DATE,
                   date_2 DATE,
                   PERIOD FOR date_period(date_1, date_2));

-- Enforce non-overlapping periods
CREATE OR REPLACE TABLE rooms (
                                  room_number INT,
                                  guest_name VARCHAR(255),
                                  checkin DATE,
                                  checkout DATE,
                                  PERIOD FOR p(checkin,checkout),
                                  UNIQUE (room_number, p WITHOUT OVERLAPS)
);

INSERT INTO rooms VALUES
                      (1, 'Regina', '2020-10-01', '2020-10-03'),
                      (2, 'Cochise', '2020-10-02', '2020-10-05'),
                      (1, 'Nowell', '2020-10-03', '2020-10-07'),
                      (2, 'Eusebius', '2020-10-06', '2020-10-07');

select * from rooms;