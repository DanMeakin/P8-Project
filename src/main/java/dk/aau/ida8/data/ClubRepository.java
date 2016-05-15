package dk.aau.ida8.data;


import dk.aau.ida8.model.Club;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Club data
 * persisted within the database.
 */
@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {
    /**
     * Defines a query for finding one club by name.
     *
     * @param name the name of the club to search for
     * @return the club found as a result of the search
     */
    Club findByName(String name);
}
