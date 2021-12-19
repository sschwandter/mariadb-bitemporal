package at.xion.mariadbbitemporal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;


public interface PersonRepository extends CrudRepository<Person, Long> {

}