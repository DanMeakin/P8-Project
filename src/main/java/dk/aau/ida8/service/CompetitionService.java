package dk.aau.ida8.service;

//This service layer handles the explicit access given to approved methods

import dk.aau.ida8.data.CompetitionRepository;
import dk.aau.ida8.model.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//service annotation to tell Spring that this is a service-class
@Service
//Service-class CompetitionService creation
public class CompetitionService {

    //Instance variable instantiation
    private CompetitionRepository competitionRepository;

    //Autowired annotation to tell Spring to create new instance of competitionRepository
    @Autowired
    //Constructor to instantiate class
    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    //Method to find all the competitions
    public Iterable<Competition> findAll() {
        return competitionRepository.findAll();
    }

    //Method to find one competition based on the competition ID
    public Competition findOne(Long id) {
        return competitionRepository.findOne(id);
    }


    //Method to save a competition. Takes a competition object and saves in database.
    public Competition save(Competition competition){
        return competitionRepository.save(competition);
    }

    //Method to delete a competition
    public void delete (Long id){
        competitionRepository.delete(id);
    }
}
