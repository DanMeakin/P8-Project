package dk.aau.ida8.service;

import dk.aau.ida8.data.ParticipantRepository;
import dk.aau.ida8.model.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    // instance variable to instantiate ParticipantRepositry
    private ParticipantRepository participantRepository;

    // Constructor to create a new ParticipantService object
    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    /** Method to find all participants in the system
     *
     * @return
     */
    public Iterable <Participant> findAll(){
        return participantRepository.findAll();
    }

    /**Method to find one specific lifter in the system based on the lifter ID
    * Lifter = find a lifter object based on ID **/
    public Participant findOne(Long id){
        return participantRepository.findOne(id);
    }

    //Save method to save a lifter-object to a database
    public Participant saveParticipant(Participant participant){
        return participantRepository.save(participant);
    }

    //Delete method to delete a lifter-object from a database
    public void deleteParticipant(Long id){
        participantRepository.delete(id);
    }
}
