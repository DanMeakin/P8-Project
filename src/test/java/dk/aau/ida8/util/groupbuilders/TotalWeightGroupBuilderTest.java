package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.model.Participant;
import dk.aau.ida8.util.groupcomparators.CompetingComparator;
import org.junit.Test;

import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TotalWeightGroupBuilderTest {

    // Values used to create Competition.
    private static Competition competition;
    private static List<Participant> maleParticipants;
    private static List<Participant> femaleParticipants;
    private static GroupBuilder builder;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // instantiate the class under test
        competition = mock(Competition.class);
        when(competition.getCompetitionType()).thenReturn(Competition.CompetitionType.TOTAL_WEIGHT);

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
                when(p.getGender()).thenReturn(Lifter.Gender.MALE);
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
                when(p.getGender()).thenReturn(Lifter.Gender.FEMALE);
                when(p.getWeightClass()).thenReturn(1);
                femaleParticipants.add(p);
            }
            allParticipants.add(p);
        }
        when(competition.getParticipants()).thenReturn(allParticipants);
        builder = new TotalWeightGroupBuilder(competition);
    }

    @Test
    public void createRankingGroups() throws Exception {
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
        expectedGs.add(new Group(competition, new ArrayList<>(femaleParticipants), Group.ComparatorType.TOTAL_WEIGHT_RANKING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(0, 15)), Group.ComparatorType.TOTAL_WEIGHT_RANKING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(15, 30)), Group.ComparatorType.TOTAL_WEIGHT_RANKING));
        List<Group> actualGs = builder.createRankingGroups();
        for (int i = 0; i < expectedGs.size(); i++) {
            assertEquals(
                    expectedGs.get(i).getParticipants(),
                    actualGs.get(i).getParticipants()
            );
        }
    }

    @Test
    public void createCompetingGroups() throws Exception {
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
        expectedGs.add(new Group(competition, new ArrayList<>(femaleParticipants), Group.ComparatorType.COMPETING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(0, 8)), Group.ComparatorType.COMPETING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(8, 15)), Group.ComparatorType.COMPETING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(15, 23)), Group.ComparatorType.COMPETING));
        expectedGs.add(new Group(competition, new ArrayList<>(maleParticipants.subList(23, 30)), Group.ComparatorType.COMPETING));
        List<Group> actualGs = builder.createCompetingGroups();
        for (int i = 0; i < expectedGs.size(); i++) {
            assertEquals(
                    expectedGs.get(i).getParticipants(),
                    actualGs.get(i).getParticipants()
            );
        }
    }

}