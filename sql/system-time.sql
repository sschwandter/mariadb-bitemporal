-- From https://mariadb.com/kb/en/system-versioned-tables/
-- Standard SQL:2011
CREATE TABLE t(
   x INT,
   start_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW START,
   end_timestamp TIMESTAMP(6) GENERATED ALWAYS AS ROW END,
   PERIOD FOR SYSTEM_TIME(start_timestamp, end_timestamp)
) WITH SYSTEM VERSIONING;


-- MariaDB version - no "start_timestamp" and "end_timestamp" columns visible by default, can be viewed with
-- pseudo-columns ROW_START and ROW_END
CREATE TABLE t (
    x INT
) WITH SYSTEM VERSIONING;

-- insert some data
insert into t (x) value (1);

-- update
update t set x=2 where x=1;

-- show timestamps
SELECT x, ROW_START, ROW_END FROM t;

-- show all historical data (MariaDB specific)

select x, ROW_START, ROW_END FROM t for system_time all;

-- show data for a certain time stamp

SELECT * FROM t FOR SYSTEM_TIME AS OF TIMESTAMP'2021-12-17 9:17:00';

SELECT * FROM t FOR SYSTEM_TIME AS OF TIMESTAMP'2021-12-17 9:18:00';

-- system-versioning implicitly add the row_end column to the Primary Key!


-- Auto partitioning current data and historical data in separate partitions

CREATE TABLE t_part_auto (x INT) WITH SYSTEM VERSIONING
    PARTITION BY SYSTEM_TIME;

-- Specify number of partitions
CREATE TABLE t_part_auto_custom_partitions (x INT) WITH SYSTEM VERSIONING
    PARTITION BY SYSTEM_TIME
        INTERVAL 1 MONTH
        PARTITIONS 12;

