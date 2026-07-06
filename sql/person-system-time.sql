-- Create person table

-- Hibernate (6+) maps @GeneratedValue(strategy = AUTO) to a database sequence named <table>_seq
-- with an allocation size of 50. MariaDB supports sequences since 10.3.
CREATE SEQUENCE person_seq INCREMENT BY 50 START WITH 1;

CREATE TABLE person (
                  id bigint,
                  name varchar(255),
                  start_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW START,
                  end_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW END,
                  PERIOD FOR SYSTEM_TIME(start_timestamp, end_timestamp)
) WITH SYSTEM VERSIONING;