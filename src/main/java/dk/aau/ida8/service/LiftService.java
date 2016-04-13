package dk.aau.ida8.service;

import dk.aau.ida8.data.LiftRepository;
import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Lifter;
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

    /**Method to find all lifts in the system
     * Iterable is like a kind of like "for-loop" iterating through a list
     * Returns all lifts in the system**/
    public Iterable <Lift> findAll(){
        return liftRepository.findAll();
    }

    /**Method to find one specific lifts in the system based on the lifts ID
     * Lifts = find a lifts object based on ID **/
    public Lift findOne(Long id){
        return liftRepository.findOne(id);
    }

    //Save method to save a lifts-object to a database
    public Lift saveLifter(Lift lift){
        return liftRepository.save(lift);
    }

}
