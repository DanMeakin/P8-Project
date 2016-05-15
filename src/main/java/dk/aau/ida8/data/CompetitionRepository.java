package dk.aau.ida8.data;

import dk.aau.ida8.model.Competition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Competition data
 * persisted within the database.
 */
@Repository
public interface CompetitionRepository extends CrudRepository<Competition, Long> {
}