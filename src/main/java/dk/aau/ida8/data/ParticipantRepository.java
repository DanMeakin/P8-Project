package dk.aau.ida8.data;

import dk.aau.ida8.model.Participant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Participant data
 * persisted within the database.
 */
@Repository
public interface ParticipantRepository extends CrudRepository<Participant, Long> {
}
