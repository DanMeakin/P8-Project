package dk.aau.ida8.service;

import dk.aau.ida8.data.CompetitionRepository;
import dk.aau.ida8.model.Competition;
import org.springframework.beans.factory.annotation.Autowired;

public class CompetitionService {

    private CompetitionRepository competitionRepository;

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public Iterable<Competition> findAll() {
        return competitionRepository.findAll();
    }

    public Competition findOne(Long id) {
        return competitionRepository.findAll(id);
    }
}
