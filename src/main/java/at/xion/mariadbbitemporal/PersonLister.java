package at.xion.mariadbbitemporal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PersonLister implements CommandLineRunner {

	private final PersonRepository repository;

	public PersonLister(PersonRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) throws Exception {
		repository.findAll().forEach(System.out::println);
	}
}
