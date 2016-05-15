package dk.aau.ida8.data;

import dk.aau.ida8.model.Lift;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Lift data
 * persisted within the database.
 */
@Repository
public interface LiftRepository extends CrudRepository<Lift, Long> {
}
