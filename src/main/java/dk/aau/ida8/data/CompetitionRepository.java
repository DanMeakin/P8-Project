package dk.aau.ida8.data;

//Simple interface that provides access to "crud" (create, read, update, delete) functions

import dk.aau.ida8.model.Competition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//Annotation to create a repository
@Repository
public interface CompetitionRepository extends CrudRepository<Competition, Long> {

}