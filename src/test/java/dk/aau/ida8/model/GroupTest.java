package dk.aau.ida8.model;

import dk.aau.ida8.util.groupcomparators.CompetingComparator;
import dk.aau.ida8.util.groupcomparators.SinclairRankingComparator;

import org.junit.BeforeClass;
import org.junit.Test;

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
            when(p.getStartingSnatchWeight()).thenReturn(snatchWeights.get(i));
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
    public void sortParticipants() throws Exception {
        // Test competing group
        Group g = new Group(totalWeightCompetition, femaleParticipants, Group.ComparatorType.COMPETING);
        femaleParticipants.sort(new CompetingComparator());
        assertEquals(femaleParticipants, g.getParticipants());

        // Test ranking group
        Group h = new Group(totalWeightCompetition, femaleParticipants, Group.ComparatorType.SINCLAIR_RANKING);
        femaleParticipants.sort(new SinclairRankingComparator());
        assertEquals(femaleParticipants, h.getParticipants());
    }

    @Test
    public void containsParticipant() throws Exception {
        Group g = new Group(totalWeightCompetition, maleParticipants, Group.ComparatorType.COMPETING);
        for (Participant p : maleParticipants) {
            assertTrue(g.containsParticipant(p));
        }
        for (Participant p : femaleParticipants) {
            assertFalse(g.containsParticipant(p));
        }
    }
}