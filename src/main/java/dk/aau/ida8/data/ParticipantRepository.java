package dk.aau.ida8.data;

import dk.aau.ida8.model.Participant;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, Long> {
}
