package dk.aau.ida8.data;

//Simple interface that provides access to "crud" (create, read, update, delete) functions

import dk.aau.ida8.model.Lifter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LifterRepository extends CrudRepository <Lifter, Long> {

}
