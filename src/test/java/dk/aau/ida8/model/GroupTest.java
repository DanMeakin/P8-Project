package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GroupTest {

    private static List<Group> expectedGroupList;
    private static List<Participant> expectedParticipantList;

    private static double[] listOfBodyWeights1Males = {60.0, 34.0, 55.6, 43.8, 54.8, 56.0, 56.1, 58.0, 54.0, 59.0,
            60.0};
    private static double[] listOfBodyWeights1Females = {48, 52, 49, 50, 51};

    private static double[] listOfBodyWeights2Males = {62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64, 62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64};
    private static double[] listOfBodyWeights2Females = {57, 56, 55, 54, 53, 57, 56, 55, 54, 53, 56};
    private static int[] listOfStartingWeights = {162, 164, 167, 165, 164, 165, 166, 168, 167, 163, 164, 162, 164, 167, 165, 164, 165, 166, 168, 167, 163, 164};

    private static Competition competition;
    private static Lifter lifter;

    private static Club club;
    private static Address address;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        club = mock(Club.class);
        address = mock(Address.class);

        competition = new CompetitionTotalWeight("Name", club, address, Competition.CompetitionType.TOTAL_WEIGHT, Date.valueOf(LocalDate.of(2016, 6, 1)), Date.valueOf(LocalDate.of(2016, 5, 15)), 100);

        expectedParticipantList = new ArrayList<>();


        for (int i = 0; i < 5; i++) {
            lifter = mock(Lifter.class);
            when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);
            when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights1Females[i]);

            competition.addParticipant(lifter, listOfStartingWeights[i]);

            expectedParticipantList.add(competition.selectParticipationByLifter(lifter));
        }

        Collections.sort(expectedParticipantList, (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight());
        Participant p1 = expectedParticipantList.get(0);
        Participant p2 = expectedParticipantList.get(1);
        Participant p3 = expectedParticipantList.get(2);
        Participant p4 = expectedParticipantList.get(3);
        Participant p5 = expectedParticipantList.get(4);

        p1.addPassedLift();
        p1.increaseWeight(164);


        expectedParticipantList.remove(p1);
        expectedParticipantList.add(p1);

    }


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void determineOrderOnSortByStartingWeight() throws Exception {

        competition.allocateGroups();
        Group group = competition.getGroupList().get(0);

        group.sortParticipants();
        Participant p1 = group.sortParticipants().get(0);
        p1.increaseWeight(164);

        assertEquals(expectedParticipantList, group.getParticipants());
    }
}