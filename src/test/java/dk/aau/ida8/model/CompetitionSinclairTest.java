package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CompetitionSinclairTest {

    private static List<List<Participant>> ExpectedList;

    // initialize arrays of data used during test data creation
    private static int[] listOfStartingWeights = {162, 164, 167, 165, 161, 165, 166, 168, 167, 163, 164};

    // declare variables needed to setup the test cases
    private static Lifter lifter;
    private static Club club;
    private static Address address;
    private static Competition competition;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // mock required objects to setup competition class under test
        club = mock(Club.class);
        address = mock(Address.class);

        // instantiate the class under test
        competition = new CompetitionSinclair(
                "lol", club, address, Competition.CompetitionType.SINCLAIR, new Date(), new Date(), 50
        );


        /*************************************

         SETUP TEST DATA

         *************************************/

        // initialize the list of expected results
        ExpectedList = new ArrayList<>();
        ExpectedList.add(new ArrayList<>());
        ExpectedList.add(new ArrayList<>());
        ExpectedList.add(new ArrayList<>());
        ExpectedList.add(new ArrayList<>());

        // get a reference to the lists that will consist of FEMALE participants
        List<Participant> firstExpectedList= ExpectedList.get(0);
        List<Participant> secondExpectedList = ExpectedList.get(1);
        // get a reference to the lists that will consist of MALE participants
        List<Participant> thirdExpectedList = ExpectedList.get(2);
        List<Participant> fourthExpectedList = ExpectedList.get(3);

        // adding FEMALE participants to the first list
        System.out.println("Adding females...");
        for (int i = 0; i < 11; i++) {
            lifter = mock(Lifter.class);
            when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);

            competition.addParticipant(lifter, listOfStartingWeights[i]);

            Participant p = competition.selectParticipationByLifter(lifter);
            firstExpectedList.add(p);
            System.out.println("FEMALE Participant " + i + " with a starting weight of " + p.getStartingWeight() + " added.");
        }

        // sort the first FEMALE list in terms of starting weight
        Collections.sort(firstExpectedList, (p1,p2) -> p1.getStartingWeight() - p2.getStartingWeight());

        // handle the case of the last FEMALE participant,
        // that is, move the last FEMALE participant to the next list
        int lastIndexOfFirstListFemale = firstExpectedList.size() - 1;
        Participant lastParticipantFemale = firstExpectedList.get(lastIndexOfFirstListFemale);
        firstExpectedList.remove(lastParticipantFemale);
        secondExpectedList.add(lastParticipantFemale);


        // adding MALE participants to the third list
        System.out.println("Adding males...");
        for (int i = 0; i < 11; i++) {
            lifter = mock(Lifter.class);
            when(lifter.getGender()).thenReturn(Lifter.Gender.MALE);

            competition.addParticipant(lifter, listOfStartingWeights[i]);

            Participant p = competition.selectParticipationByLifter(lifter);
            thirdExpectedList.add(p);
            System.out.println("MALE Participant " + i + " with a starting weight of " + p.getStartingWeight() + " added.");
        }

        // sort the first MALE list in terms of starting weight
        Collections.sort(thirdExpectedList, (p1,p2) -> p1.getStartingWeight() - p2.getStartingWeight());

        // handle the case of the last MALE participant,
        // that is, move the last MALE participant to the next list
        int lastIndexOfFirstListMale = thirdExpectedList.size() - 1;
        Participant lastParticipantMale = thirdExpectedList.get(lastIndexOfFirstListMale);
        thirdExpectedList.remove(lastParticipantMale);
        fourthExpectedList.add(lastParticipantMale);
    }


    @Before
    public void setUp() throws Exception {


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
        // split the expected lists up
        List<Participant> firstExpectedList = ExpectedList.get(0);
        List<Participant> secondExpectedList = ExpectedList.get(1);
        List<Participant> thirdExpectedList = ExpectedList.get(2);
        List<Participant> fourthExpectedList = ExpectedList.get(3);

        // get the actual list
        List<List<Participant>> actualList = competition.allocateGroups();

        // split up the actual list
        List<Participant> firstSimpleActualList = actualList.get(0);
        List<Participant> secondSimpleActualList = actualList.get(1);
        List<Participant> thirdSimpleActualList = actualList.get(2);
        List<Participant> fourthSimpleActualList = actualList.get(3);

        assertEquals(firstExpectedList, firstSimpleActualList);
        assertEquals(secondExpectedList, secondSimpleActualList);
        assertEquals(thirdExpectedList, thirdSimpleActualList);
        assertEquals(fourthExpectedList, fourthSimpleActualList);

        // it actually works comparing nested lists!!!
        assertEquals(ExpectedList, actualList);
    }

    @Test
    public void testCalculateRankings() throws Exception {
        ArrayList<Participant> pList = (ArrayList<Participant>) competition.calculateRankings();

    }

    /*
    @Test
    public void testGetRank() throws Exception{
        assertEquals(1, p1.getRank());
        assertEquals(2, p2.getRank());
    }*/

}