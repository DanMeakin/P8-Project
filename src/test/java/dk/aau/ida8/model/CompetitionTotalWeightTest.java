package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Created by folmer on 4/14/16.
 */
public class CompetitionTotalWeightTest {

    // The mocked list of participants
    private static List<Participant> listOfParticipants;
    private static double[] listOfBodyWeights = {60.0, 34.0, 55.6, 43.8, 54.8, 56.0, 56.1, 58.0, 54.0, 59.0,
            60.0};

    private static double[] listOfBodyWeights2 = {62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64,};
    private static CompetitionTotalWeight competition;
    private static Participant participant;

    private static List<List<Participant>> listOfLists;

    private static Club club;
    private static Address address;


    @Before
    public void setUp() throws Exception {

        listOfParticipants = new ArrayList<>();
        listOfLists = new ArrayList<>();
        listOfLists.add(new ArrayList<Participant>());
        listOfLists.add(new ArrayList<Participant>());

        for(int j = 0; j < 2; j++) {
            for (int i = 0; i < 11; i++) {
                participant = mock(Participant.class);
                //when(participant.getGender()).thenReturn(Lifter.Gender.MALE);
                when(participant.getBodyWeight()).thenReturn(listOfBodyWeights[i]);

                listOfParticipants.add(participant);
            }
        }

        for(int j = 0; j < 2; j++) {
            for (int i = 0; i < 11; i++) {
                participant = mock(Participant.class);
                when(participant.getGender()).thenReturn(Lifter.Gender.MALE);
                when(participant.getBodyWeight()).thenReturn(listOfBodyWeights2[i]);

                listOfParticipants.add(participant);
            }
        }

        for (int i = 0; i < listOfParticipants.size()-1; i++) {
            listOfLists.get(0).add(listOfParticipants.get(i));
        }

        listOfLists.get(1).add(listOfParticipants.get(listOfParticipants.size() - 1));

        club = mock(Club.class);
        address = mock(Address.class);

        competition = new CompetitionTotalWeight("Name", club, address, Competition.CompetitionType.TOTAL_WEIGHT, Date.valueOf(LocalDate.of(2016, 6, 1)), Date.valueOf(LocalDate.of(2016, 5, 15)), 100);


    }

    @Test
    public void allocateGroupsTest() throws Exception {

        // to be continued...

        //assertEquals(listOfLists, competition.);

    }

    @Test
    public void createSubGroupsTest() throws Exception {
        List<List<Participant>> completeList = competition.createSubgroups((ArrayList<Participant>) listOfParticipants);
        System.out.println("The size of the completeList is " + completeList.size());
        for(List<Participant> lp : completeList) {
            System.out.println("This is a sublist: ");
            for(Participant p : lp) {
                System.out.println(p.getBodyWeight());
            }
        }
        //System.out.println(completeList.get(1).get(0).getBodyWeight());

        competition.allocateGroups(listOfParticipants);
    }
}