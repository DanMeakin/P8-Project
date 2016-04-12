package dk.aau.ida8.service;

import dk.aau.ida8.data.LiftRepository;
import dk.aau.ida8.model.Lift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

/**
 * Created by nicklas on 17-03-16.
 */
@Service
public class LiftService {

    private LiftRepository liftRepository;

    @Autowired
    public LiftService(LiftRepository liftRepository) {
        this.liftRepository = liftRepository;
    }

    public Iterable<Lift> findAll(){
        return liftRepository.findAll();
    }
}
