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

        Address address = new Address("", "Østerbro 33", "Aalborg", "9000");
        addressRepository.save(address);
        Club club = new Club("AK Jyden", address);
        clubRepository.save(club);

        Address address1 = new Address("", "Nygade 114", "København K", "4000");
        addressRepository.save(address1);
        Club club1 = new Club("AK Viking", address1);
        clubRepository.save(club1);

        Address address2 = new Address("", "Petergade 1", "Århus", "8210");
        addressRepository.save(address2);
        Club club2 = new Club("AK Viking", address2);
        clubRepository.save(club2);

        Lifter lifter2 = new Lifter("Lotte", "S", club, Lifter.Gender.FEMALE, new Date(), 60.0);
        lifterRepository.save(lifter2);
        club.addLifter(lifter2);

        Lifter lifter3 = new Lifter("Robin", "L", club1, Lifter.Gender.MALE, new Date(), 60.0);
        lifterRepository.save(lifter3);
        club1.addLifter(lifter3);

        Competition c1 = new CompetitionSinclair("Super Awesome Competition!", club, address, Competition.CompetitionType.SINCLAIR, new Date(), new Date(), 50);
        competitionRepository.save(c1);

        Competition c2 = new CompetitionTotalWeight("Super Awesome Competition!", club1, address1, Competition.CompetitionType.TOTAL_WEIGHT, new Date(), new Date(), 50);
        competitionRepository.save(c2);

        c1.addParticipant(lifter2, 119);
        c1.addParticipant(lifter3, 120);
        competitionRepository.save(c1);
    }
}