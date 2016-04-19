package dk.aau.ida8.data;


import dk.aau.ida8.model.Club;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {

    Club findByName(String name);

}
