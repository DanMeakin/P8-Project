package dk.aau.ida8.data;

import dk.aau.ida8.model.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents the Repository for accessing Address data
 * persisted within the database.
 */
@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {
}
