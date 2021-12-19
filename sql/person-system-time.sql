-- Create person table

CREATE TABLE person (
                  id bigint,
                  name varchar(255),
                  start_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW START,
                  end_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW END,
                  PERIOD FOR SYSTEM_TIME(start_timestamp, end_timestamp)
) WITH SYSTEM VERSIONING;