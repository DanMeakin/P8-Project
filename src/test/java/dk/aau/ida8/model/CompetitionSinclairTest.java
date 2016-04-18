package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dk.aau.ida8.model.Lifter.Gender.MALE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mikkelmoerch on 12/04/16.
 */
public class CompetitionSinclairTest {

    ArrayList<Participant> list;
    Competition competition;

    private static List<Participant> listOfParticipants;
    private static List<List<Participant>> checkList;
    private static double[] listOfBodyWeights = {60.0, 34.0, 55.6, 43.8, 54.8, 56.0, 56.1, 58.0, 54.0, 59.0,
            60.0};

    private static double[] listOfBodyWeights2 = {62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64,};
    private static int[] listOfStartingWeights = {162, 164, 167, 165, 164, 165, 166, 168, 167, 163, 164,};

    Lifter lifter;

    Participant p2;

    Club club;
    Address address;

    @Before
    public void setUp() throws Exception {


        // Compare list
        checkList = new ArrayList<>();

        club = mock(Club.class);
        address = mock(Address.class);

        competition = new CompetitionSinclair("lol", club, address, Competition.CompetitionType.SINCLAIR, new Date(), new Date(), 50);



        // Populating list with Female lifters in weight class 1
        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfPs = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfPs.add(competition.selectParticipationByLifter(lifter));
            }

            checkList.add(subListOfPs);
        }

        // Populating list with Female lifters in weight class 2
        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfps = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights2[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfps.add(competition.selectParticipationByLifter(lifter));

            }

            checkList.add(subListOfps);
        }

        // Populating list with Male lifters in weight class 2
        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfps = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.MALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights2[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfps.add(competition.selectParticipationByLifter(lifter));
            }

            checkList.add(subListOfps);
        }






        /*
        // Updating the competition list so its equal to the compare list
        c.addParticipant(l, 70);
        c.addParticipant(l2, 71);

        p1 = c.selectParticipationByLifter(l);
        p2 = c.selectParticipationByLifter(l2);

        p1.addPassedLift();
        p1.addPassedLift();
        p1.addPassedLift();
        p1.addPassedLift();

        p2.addFailedLift();
        p2.addFailedLift();
        p2.addFailedLift();
        p2.addFailedLift();
        p2.addFailedLift();
        p2.addFailedLift();
        */

        /*
        p1.setCurrentWeight(71);
        p1.addLift(Lift.LiftType.SNATCH, true);


        p1.setCurrentWeight(71);
        p1.addLift(Lift.LiftType.CLEANANDJERK, true);

        // P2 has a higher score than p1 with the following accepted lifts
        p2.setCurrentWeight(72);
        p2.addLift(Lift.LiftType.SNATCH, true);

        p2.setCurrentWeight(73);
        p2.addLift(Lift.LiftType.CLEANANDJERK, true);

        // Manually adding p1 and p2 to list in incorrect ranked order
        list.add(p1);
        list.add(p2);

        // Manually adding p1 and p2 to list in correct ranked order
        correctList = new ArrayList<>();
        correctList.add(p2);
        correctList.add(p1);

        */

        /**
         * There is a bug somewhere which can be triggered by
         * a scenario where p1 lifting before p2 with weights
         * higher than those of p2.
         * An IllegalArgumentException is thrown.
         *
         * Comment the scenario above and de-comment the
         * scenario above to run tests and see exception
         */

        /*
        // P1 has a higher score than p2 with the following accepted lifts
        p1.setCurrentWeight(71);
        p1.addLift(Lift.LiftType.SNATCH, true);

        p1.setCurrentWeight(75);
        p1.addLift(Lift.LiftType.CLEANANDJERK, true);

        p2.setCurrentWeight(69);
        p2.addLift(Lift.LiftType.SNATCH, true);

        p2.setCurrentWeight(72);
        p2.addLift(Lift.LiftType.CLEANANDJERK, true);

        // Manually adding p1 and p2 to list in incorrect ranked order
        list.add(p2);
        list.add(p1);

        // Manually adding p1 and p2 to list in correct ranked order
        correctList = new ArrayList<>();
        correctList.add(p1);
        correctList.add(p2);
        */
    }

    @Test
    public void testAllocateGroups() throws Exception {


    }

    @Test
    public void testCalculateRankings() throws Exception {
        ArrayList<Participant> pList = (ArrayList<Participant>) competition.calculateRankings();

        //assertNotEquals(list, pList);

        //assertEquals(correctList, pList);
    }

    /*
    @Test
    public void testGetRank() throws Exception{
        assertEquals(1, p1.getRank());
        assertEquals(2, p2.getRank());
    }*/

}