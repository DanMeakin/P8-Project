package dk.aau.ida8;

import dk.aau.ida8.data.LifterRepository;
import dk.aau.ida8.model.Address;
import dk.aau.ida8.model.Club;
import dk.aau.ida8.model.Lifter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataLoader {

    private LifterRepository lifterRepository;

    @Autowired
    public DataLoader(LifterRepository lifterRepository) {
        this.lifterRepository = lifterRepository;
    }

    @PostConstruct
    public void loadData() {
        lifterRepository.deleteAll();
        createLifters();
    }

    public void createLifters() {

        /*
        Lifter lifter1 = new Lifter();
        lifter1.setLifterNumber(11);
        lifter1.setName("Mikkel");
        lifter1.setGender("M");
        lifter1.setClub("AK Jyden");
        lifterRepository.save(lifter1);

        Lifter lifter2 = new Lifter();
        lifter2.setLifterNumber(12);
        lifter2.setName("Lotte");
        lifterRepository.save(lifter2);

        Lifter lifter3 = new Lifter();
        lifter3.setLifterNumber(9);
        lifter3.setName("Robin");
        lifterRepository.save(lifter3);
*/

        Club club = new Club("AK Jyden", new Address("", "Vesterbro 33", "Aalborg", "9000"));
        Lifter lifter2 = new Lifter("Lotte", "S", club, Lifter.Gender.FEMALE, 60.0);
        lifterRepository.save(lifter2);

        Lifter lifter3 = new Lifter("Robin", "L", club, Lifter.Gender.MALE, 60.0);
        lifterRepository.save(lifter3);

    }
}