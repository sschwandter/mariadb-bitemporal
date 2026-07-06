package at.xion.mariadbbitemporal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the system-time (transaction-time) axis end-to-end through JPA:
 * MariaDB archives old row versions automatically, the repository only ever
 * sees current state, and history/time travel is reachable via native SQL
 * ({@code FOR SYSTEM_TIME} has no JPQL equivalent).
 *
 * <p>The test transaction is disabled ({@code NOT_SUPPORTED}) on purpose:
 * MariaDB stamps all row versions written by one transaction with the same
 * timestamp, so insert and update must commit separately for a history row
 * with a non-empty period to exist.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
public class SystemTimeIT {

	@Container
	@ServiceConnection
	static MariaDBContainer mariadb = new MariaDBContainer("mariadb:12.3")
			.withInitScript("person-system-time.sql");

	@Autowired
	private PersonRepository repository;

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void updateArchivesOldVersionAndAllowsTimeTravel() {
		// insert
		Person person = new Person();
		person.setName("Ada Lovelace");
		Long id = repository.save(person).getId();
		assertThat(id).isNotNull();

		// a fresh read sees the DB-generated system-time columns
		Person current = repository.findById(id).orElseThrow();
		assertThat(current.getStartTimestamp()).isNotNull();
		assertThat(current.getEndTimestamp()).isNotNull();

		// update (this is what used to fail with MariaDB error 1906 before the
		// system-time columns were mapped insertable = false, updatable = false)
		current.setName("Ada King");
		repository.save(current);

		// the repository sees only the current version
		assertThat(repository.findAll())
				.hasSize(1)
				.first()
				.extracting(Person::getName)
				.isEqualTo("Ada King");

		// full history via FOR SYSTEM_TIME ALL: both versions, seamless periods
		List<Map<String, Object>> versions = jdbc.queryForList("""
				SELECT name, start_timestamp, end_timestamp
				FROM person FOR SYSTEM_TIME ALL
				WHERE id = ?
				ORDER BY start_timestamp
				""", id);
		assertThat(versions).hasSize(2);
		assertThat(versions.get(0).get("name")).isEqualTo("Ada Lovelace");
		assertThat(versions.get(1).get("name")).isEqualTo("Ada King");
		assertThat(versions.get(0).get("end_timestamp"))
				.isEqualTo(versions.get(1).get("start_timestamp"));

		// time travel: AS OF the instant the first version became current
		Timestamp beforeUpdate = (Timestamp) versions.get(0).get("start_timestamp");
		String nameThen = jdbc.queryForObject("""
				SELECT name FROM person FOR SYSTEM_TIME AS OF ?
				WHERE id = ?
				""", String.class, beforeUpdate, id);
		assertThat(nameThen).isEqualTo("Ada Lovelace");
	}
}
