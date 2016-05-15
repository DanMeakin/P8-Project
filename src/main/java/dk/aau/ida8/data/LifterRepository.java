package dk.aau.ida8.data;

import dk.aau.ida8.model.Lifter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Lifter data
 * persisted within the database.
 */
@Repository
public interface LifterRepository extends CrudRepository <Lifter, Long> {
}
