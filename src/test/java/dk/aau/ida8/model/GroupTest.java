package dk.aau.ida8.model;

import dk.aau.ida8.model.groupComparators.CompetingComparator;
import dk.aau.ida8.model.groupComparators.SinclairRankingComparator;
import dk.aau.ida8.util.Tuple;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dk.aau.ida8.model.Lifter.Gender.FEMALE;
import static dk.aau.ida8.model.Lifter.Gender.MALE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GroupTest {

    // Values used to create Competition.
    private static Competition sinclairCompetition;
    private static Competition totalWeightCompetition;
    private static List<Participant> maleParticipants;
    private static List<Participant> femaleParticipants;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // instantiate the class under test
        sinclairCompetition = mock(Competition.class);
        when(sinclairCompetition.getCompetitionType()).thenReturn(Competition.CompetitionType.SINCLAIR);

        totalWeightCompetition = mock(Competition.class);
        when(totalWeightCompetition.getCompetitionType()).thenReturn(Competition.CompetitionType.TOTAL_WEIGHT);

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
        List<Participant> allParticipants = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            Participant p = mock(Participant.class);
            Lifter l = mock(Lifter.class);
            when(l.getId()).thenReturn((long) i);
            when(p.getCurrentWeight()).thenReturn(snatchWeights.get(i));
            when(p.getStartingSnatchWeight()).thenReturn(snatchWeights.get(i));
            when(p.getStartingWeight()).thenReturn(snatchWeights.get(i));
            when(p.getLifter()).thenReturn(l);
            // Create 30 male lifters
            if (i < 30) {
                when(p.getGender()).thenReturn(MALE);
                // Create 15 male lifters in WC1
                if (i < 15) {
                    when(p.getWeightClass()).thenReturn(1);
                    // and create 15 male lifters in WC2
                } else {
                    when(p.getWeightClass()).thenReturn(2);
                }
                maleParticipants.add(p);
                // and create 10 female lifters
            } else {
                when(p.getGender()).thenReturn(FEMALE);
                when(p.getWeightClass()).thenReturn(1);
                femaleParticipants.add(p);
            }
            allParticipants.add(p);
        }
        when(sinclairCompetition.getParticipants()).thenReturn(allParticipants);
        when(totalWeightCompetition.getParticipants()).thenReturn(allParticipants);
    }

    @Test
    public void createSinclairCompetingGroups() throws Exception {
        // Sort by starting weight
        Collections.sort(
                maleParticipants,
                (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight()
        );
        Collections.sort(
                femaleParticipants,
                (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight()
        );

        List<Group> expectedGs = new ArrayList<>();
        expectedGs.add(Group.competingGroup(sinclairCompetition, femaleParticipants));
        for (int i = 0; i < 3; i++) {
            expectedGs.add(Group.competingGroup(sinclairCompetition, new ArrayList<>(maleParticipants.subList(i*10, (i+1)*10))));
        }
        List<Group> actualGs = Group.createCompetingGroups(sinclairCompetition);
        assertEquals(expectedGs, actualGs);
    }

    @Test
    public void createSinclairRankingGroups() throws Exception {
        // Sort by starting weight
        Collections.sort(
                maleParticipants,
                (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight()
        );
        Collections.sort(
                femaleParticipants,
                (p1, p2) -> p1.getStartingWeight() - p2.getStartingWeight()
        );

        List<Group> expectedGs = new ArrayList<>();
        expectedGs.add(Group.competingGroup(sinclairCompetition, femaleParticipants));
        expectedGs.add(Group.competingGroup(sinclairCompetition, maleParticipants));
        List<Group> actualGs = Group.createRankingGroups(sinclairCompetition);
        for (int i = 0; i < expectedGs.size(); i++) {
            assertEquals(
                    expectedGs.get(i).getParticipants(),
                    actualGs.get(i).getParticipants()
            );
        }
    }

    @Test
    public void createTotalWeightCompetingGroups() throws Exception {
        // Sort by starting weight
        Collections.sort(
                maleParticipants,
                (p1, p2) -> {
                    if (p1.getWeightClass() == p2.getWeightClass()) {
                        return p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight();
                    } else {
                        return p1.getWeightClass() - p2.getWeightClass();
                    }
                }
        );
        Collections.sort(
                femaleParticipants,
                (p1, p2) -> {
                    if (p1.getWeightClass() == p2.getWeightClass()) {
                        return p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight();
                    } else {
                        return p1.getWeightClass() - p2.getWeightClass();
                    }
                }
        );

        List<Group> expectedGs = new ArrayList<>();
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(femaleParticipants)));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(0, 10))));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(10, 15))));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(15, 25))));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(25, 30))));
        List<Group> actualGs = Group.createCompetingGroups(totalWeightCompetition);
        for (int i = 0; i < expectedGs.size(); i++) {
            assertEquals(
                    expectedGs.get(i).getParticipants(),
                    actualGs.get(i).getParticipants()
            );
        }
    }

    @Test
    public void createTotalWeightRankingGroups() throws Exception {
        // Sort by starting weight
        Collections.sort(
                maleParticipants,
                (p1, p2) -> {
                    if (p1.getWeightClass() == p2.getWeightClass()) {
                        return p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight();
                    } else {
                        return p1.getWeightClass() - p2.getWeightClass();
                    }
                }
        );
        Collections.sort(
                femaleParticipants,
                (p1, p2) -> {
                    if (p1.getWeightClass() == p2.getWeightClass()) {
                        return p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight();
                    } else {
                        return p1.getWeightClass() - p2.getWeightClass();
                    }
                }
        );

        List<Group> expectedGs = new ArrayList<>();
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(femaleParticipants)));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(0, 15))));
        expectedGs.add(Group.competingGroup(totalWeightCompetition, new ArrayList<>(maleParticipants.subList(15, 30))));
        List<Group> actualGs = Group.createRankingGroups(totalWeightCompetition);
        for (int i = 0; i < expectedGs.size(); i++) {
            assertEquals(
                    expectedGs.get(i).getParticipants(),
                    actualGs.get(i).getParticipants()
            );
        }
    }

    @Test
    public void tupleEquals() throws Exception {
        Tuple<Lifter.Gender, Integer> t1 = new Tuple<>(FEMALE, 1);
        Tuple<Lifter.Gender, Integer> t2 = new Tuple<>(FEMALE, 1);
        Tuple<Lifter.Gender, Integer> t3 = new Tuple<>(FEMALE, 2);
        Tuple<Lifter.Gender, Integer> t4 = new Tuple<>(MALE, 1);

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, t4);
    }

    @Test
    public void sortParticipants() throws Exception {
        // Test competing group
        Group g = Group.competingGroup(totalWeightCompetition, femaleParticipants);
        femaleParticipants.sort(new CompetingComparator());
        assertEquals(femaleParticipants, g.getParticipants());

        // Test ranking group
        Group h = Group.sinclairRankingGroup(totalWeightCompetition, femaleParticipants);
        femaleParticipants.sort(new SinclairRankingComparator());
        assertEquals(femaleParticipants, h.getParticipants());
    }

    @Test
    public void containsParticipant() throws Exception {
        Group g = Group.competingGroup(totalWeightCompetition, maleParticipants);
        for (Participant p : maleParticipants) {
            assertTrue(g.containsParticipant(p));
        }
        for (Participant p : femaleParticipants) {
            assertFalse(g.containsParticipant(p));
        }
    }
}