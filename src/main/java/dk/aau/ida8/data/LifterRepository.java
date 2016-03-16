package dk.aau.ida8.data;

//Simple interface that provides access to "crud" (create, read, update, delete) functions

import org.springframework.data.repository.CrudRepository;

public interface LifterRepository extends CrudRepository <Lifter, Long> {

}
