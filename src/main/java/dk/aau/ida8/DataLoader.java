package dk.aau.ida8;

import dk.aau.ida8.data.LifterRepository;
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

        Lifter lifter2 = new Lifter("Lotte", "Ak Jyden", "F", 1, 60.0f, 70, 80);
        lifter2.updateLiftWeight(lifter2.getCleanJerks(), 2, 70);
        lifterRepository.save(lifter2);

        Lifter lifter3 = new Lifter("Robin", "Ak Jyden", "M", 2, 60.0f, 70, 80);
        lifterRepository.save(lifter3);

    }
}