package dk.aau.ida8.service;

import dk.aau.ida8.data.ClubRepository;
import dk.aau.ida8.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ClubService {

    private ClubRepository clubRepository;

    @Autowired
    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public Iterable<Club> findAll() {
        return clubRepository.findAll();
    }

    public Club findOne(Long id) {
        return clubRepository.findOne(id);
    }

}
