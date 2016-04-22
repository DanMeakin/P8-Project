package dk.aau.ida8.model;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by folmer on 4/14/16.
 */
public class CompetitionTotalWeightTest {

    // The mocked list of participants
    private static List<List<Participant>> checkList;
    private static List<Participant> femaleList1;
    private static List<Participant> femaleList2;
    private static List<Participant> maleList1;
    private static List<Participant> maleList2;
    private static List<Participant> maleList3;
    private static List<List<Participant>> mixedList;

    private static double[] listOfBodyWeights1Males = {60.0, 34.0, 55.6, 43.8, 54.8, 56.0, 56.1, 58.0, 54.0, 59.0,
            60.0};

    private static double[] listOfBodyWeights1Females = {48, 52, 49, 50, 51};

    private static double[] listOfBodyWeights2Males = {62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64, 62, 64, 67, 65, 64, 65, 66, 68, 67, 63, 64};
    private static double[] listOfBodyWeights2Females = {57, 56, 55, 54, 53, 57, 56, 55, 54, 53, 56};
    private static int[] listOfStartingWeights = {162, 164, 167, 165, 164, 165, 166, 168, 167, 163, 164, 162, 164, 167, 165, 164, 165, 166, 168, 167, 163, 164};
    private static CompetitionTotalWeight competition;
    private static Lifter lifter;

    private static Club club;
    private static Address address;



    //Test setup for createRankedGroups
    public static List<Group> expectedEmptyRankedGroups;
    //Test setup for populateRankedGroups
    public static List<Group> expectedPopulatedRankedGroups;

    @BeforeClass
    public static void setUp() throws Exception {

        club = mock(Club.class);
        address = mock(Address.class);

        competition = new CompetitionTotalWeight("Name", club, address, Competition.CompetitionType.TOTAL_WEIGHT, Date.valueOf(LocalDate.of(2016, 6, 1)), Date.valueOf(LocalDate.of(2016, 5, 15)), 100);

        checkList = new ArrayList<>();
        femaleList1 = new ArrayList<>();
        femaleList2 = new ArrayList<>();
        maleList1 = new ArrayList<>();
        maleList2 = new ArrayList<>();
        maleList3 = new ArrayList<>();
        mixedList = new ArrayList<>();

        // Populating list with Female lifters in weight class 1
        /*
        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfPs = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights1Females[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfPs.add(competition.selectParticipationByLifter(lifter));
                femaleList1.add(competition.selectParticipationByLifter(lifter));

            }

            checkList.add(subListOfPs);
        }*/

        // Populating list with Female lifters in weight class 2

        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfps = new ArrayList<>();

            for (int i = 0; i < 11; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.FEMALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights2Females[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfps.add(competition.selectParticipationByLifter(lifter));

                if(i < 10) {
                    femaleList1.add(competition.selectParticipationByLifter(lifter));
                }

                if(i >= 10){
                    femaleList2.add(competition.selectParticipationByLifter(lifter));
                }
            }

            checkList.add(subListOfps);
        }

        // Populating list with Male lifters in weight class 2
        for(int j = 0; j < 1; j++) {

            List<Participant> subListOfps = new ArrayList<>();

            for (int i = 0; i < 22; i++) {
                lifter = mock(Lifter.class);
                when(lifter.getGender()).thenReturn(Lifter.Gender.MALE);
                when(lifter.getBodyWeight()).thenReturn(listOfBodyWeights2Males[i]);

                competition.addParticipant(lifter, listOfStartingWeights[i]);

                subListOfps.add(competition.selectParticipationByLifter(lifter));

                if (i < 10) {
                    maleList1.add(competition.selectParticipationByLifter(lifter));
                }

                if(i >= 10 && i < 20){
                    maleList2.add(competition.selectParticipationByLifter(lifter));
                }

                if(i >= 20){
                    maleList3.add(competition.selectParticipationByLifter(lifter));
                }
            }

            checkList.add(subListOfps);
        }


        mixedList.add(femaleList1);
        mixedList.add(femaleList2);
        mixedList.add(maleList1);
        mixedList.add(maleList2);
        mixedList.add(maleList3);

        // Each list of lifters are sorted according to bodyweight
        for(List<Participant> l : checkList){
            Collections.sort(l, (l1, l2) -> (int) l1.getBodyWeight() - (int) l2.getBodyWeight());
        }

        //for (int i = 0; i < listOfParticipants.size()-1; i++) {
        //    listOfLists.get(0).add(listOfParticipants.get(i));
        //}

        //listOfLists.get(1).add(listOfParticipants.get(listOfParticipants.size() - 1));


        /**
         * Setup for createRankedGroupsTest
         */
        expectedEmptyRankedGroups = new ArrayList<>();
        // Adding ranked groups for females
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS1));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS2));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS3));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS4));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS5));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS6));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.FEMALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS7));

        //Adding ranked groups for males - starting at index 7
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS1));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS2));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS3));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS4));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS5));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS6));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS7));
        expectedEmptyRankedGroups.add(Group.totalWeightRankingGroup(Lifter.Gender.MALE, CompetitionTotalWeight.WEIGHTCLASS.WEIGHTCLASS8));

        /**
         * Setup for populateRankedGroupsTest
         */
        expectedPopulatedRankedGroups.addAll(expectedEmptyRankedGroups);
    }

    @Test
    public void sortWeightclassesTest() throws Exception {

    }

    @Test
    public void createRankedGroupsTest() throws Exception {
        assertEquals(expectedEmptyRankedGroups, competition.getRankedGroups());
    }

    @Test
    public void populateRankedGroupsTest() throws Exception {

    }

}