package dk.aau.ida8.model;

import org.hibernate.metamodel.relational.Tuple;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

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

    // Values used to create Competition.
    private static Competition competition;
    private static Date competitionDate;
    private static Date lastSignUpDate;
    private static int maxParticipants;
    private static List<Participant> maleParticipants;
    private static List<Participant> femaleParticipants;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // mock required objects to setup competition class under test
        club = mock(Club.class);
        address = mock(Address.class);

        competitionDate = new GregorianCalendar(2016, 6, 1).getTime();
        lastSignUpDate = new GregorianCalendar(2016, 5, 1).getTime();
        maxParticipants = 50;

        // instantiate the class under test
        competition = new CompetitionSinclair(
                "Test Sinclair",
                club,
                address,
                Competition.CompetitionType.SINCLAIR,
                competitionDate,
                lastSignUpDate,
                maxParticipants
        );

        List<Integer> snatchWeights = Arrays.asList(
                50,
                50,
                55,
                60,
                62,
                66,
                67,
                67,
                71,
                72,
                75,
                79,
                81,
                85,
                86,
                90,
                90,
                90,
                95,
                95,
                100,
                100,
                110,
                110,
                112,
                113,
                115,
                119,
                120,
                120,
                121,
                130,
                140,
                160,
                161,
                190,
                200,
                250,
                300,
                1000
        );
        Collections.shuffle(snatchWeights);

        maleParticipants = new ArrayList<>();
        femaleParticipants = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            Participant p = mock(Participant.class);
            Lifter l = mock(Lifter.class);
            when(l.getId()).thenReturn((long) i);
            when(p.getCurrentWeight()).thenReturn(snatchWeights.get(i));
            when(p.getLifter()).thenReturn(l);
            // Create 30 male lifters
            if (i < 30) {
                when(p.getGender()).thenReturn(Lifter.Gender.MALE);
                maleParticipants.add(p);
            // and create 10 female lifters
            } else {
                when(p.getGender()).thenReturn(Lifter.Gender.FEMALE);
                femaleParticipants.add(p);
            }
            competition.addParticipant(p);
        }

        // Sort by starting weight
        Collections.sort(
                maleParticipants,
                (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight()
        );


        /*************************************

         SETUP TEST DATA

         *************************************/

        /*
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
        */
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
    public void instanTests() throws Exception {



        Instant instant = Instant.now();
        Instant instant2 = Instant.now();

        instant.isAfter(instant2);

        System.out.println(instant.isAfter(instant2));

        assertNotEquals(instant, instant2);

    }

    /**
     * Tests the allocateGroups method on the CompetitionSinclair class.
     */
    @Test
    public void testAllocateGroups() throws Exception {
        List<Group> expectedGs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            expectedGs.add(Group.sinclairRankingGroup(maleParticipants.subList(i*10, (i+1)*10)));
        }
        expectedGs.add(Group.sinclairRankingGroup(femaleParticipants));
        competition.allocateGroups();
        List<Group> actualGs = competition.getGroupList();
        assertTrue(
                expectedGs.containsAll(actualGs) &&
                actualGs.containsAll(expectedGs)
        );
    }

    @Test
    public void testCalculateRankings() throws Exception {
        //ArrayList<Participant> pList = (ArrayList<Participant>) competition.calculateRankings();

    }

    /*
    @Test
    public void testGetRank() throws Exception{
        assertEquals(1, p1.getRank());
        assertEquals(2, p2.getRank());
    }*/

}