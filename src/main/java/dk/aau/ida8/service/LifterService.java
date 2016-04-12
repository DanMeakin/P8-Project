package dk.aau.ida8.service;

//This service layer will be used to grant explicit access to lifters
//Service is a layer between database and controller

import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.data.LifterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LifterService {

    //instance variable to instantiate LifterRepository
    private LifterRepository lifterRepository;

    //Constructor to create a new lifterservice object
    @Autowired
    public LifterService(LifterRepository lifterRepository){
        this.lifterRepository = lifterRepository;
    }

    /**Method to find all lifters in the system
    * Iterable is like a kind of like "for-loop" iterating through a list
    * Returns all lifters in the system**/
    public Iterable <Lifter> findAll(){
        return lifterRepository.findAll();
    }

    /**Method to find one specific lifter in the system based on the lifter ID
    * Lifter = find a lifter object based on ID **/
    public Lifter findOne(Long id){
        return lifterRepository.findOne(id);
    }

    //Save method to save a lifter-object to a database
    public Lifter saveLifter(Lifter lifter){
        return lifterRepository.save(lifter);
    }

    //Delete method to delete a lifter-object from a database
    public void deleteLifter(Long id){
        lifterRepository.delete(id);
    }
}


