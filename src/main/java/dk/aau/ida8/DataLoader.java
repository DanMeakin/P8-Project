package dk.aau.ida8;

import dk.aau.ida8.data.*;
import dk.aau.ida8.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Date;

@Component
public class DataLoader {

    private LifterRepository lifterRepository;
    private ClubRepository clubRepository;
    private AddressRepository addressRepository;
    private CompetitionRepository competitionRepository;
    private ParticipantRepository participantRepository;

    @Autowired
    public DataLoader(LifterRepository lifterRepository,
                      ClubRepository clubRepository,
                      AddressRepository addressRepository,
                      CompetitionRepository competitionRepository,
                      ParticipantRepository participantRepository) {
        this.lifterRepository = lifterRepository;
        this.clubRepository = clubRepository;
        this.addressRepository = addressRepository;
        this.competitionRepository = competitionRepository;
        this.participantRepository = participantRepository;
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

        Address address = new Address("", "oseesterbro 33", "Aalborg", "9000");
        addressRepository.save(address);
        Club club = new Club("AK Jyden", address);
        clubRepository.save(club);

        Address address1 = new Address("", "Nygade 114", "København K", "4000");
        addressRepository.save(address1);
        Club club1 = new Club("AK Viking", address1);
        clubRepository.save(club1);

        Lifter lifter2 = new Lifter("Lotte", "S", club, Lifter.Gender.FEMALE, LocalDate.of(1990, 3, 6), 60.0);
        lifterRepository.save(lifter2);

        Lifter lifter3 = new Lifter("Robin", "L", club, Lifter.Gender.MALE, LocalDate.of(1992, 10, 22), 60.0);
        lifterRepository.save(lifter3);

        Competition c1 = new CompetitionSinclair("Super Awesome Competition!", club, address, Competition.CompetitionType.SINCLAIR, new Date());
        competitionRepository.save(c1);

        Competition c2 = new CompetitionTotalWeight("Super Awesome Competition!", club1, address1, Competition.CompetitionType.TOTAL_WEIGHT, new Date());
        competitionRepository.save(c2);

        /*
        Participant p = new Participant(lifter2, c, 100);
        participantRepository.save(p);
        */

    }
}