package dk.aau.ida8.model;

import dk.aau.ida8.util.WeightClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightClassTest {

    private static List<Lifter> lifters;
    private static List<Participant> participants;

    private static List<Double> maleWeights;
    private static List<Integer> maleWeightClasses;
    private static List<Double> femaleWeights;
    private static List<Integer> femaleWeightClasses;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        maleWeightClasses = Arrays.asList(
                1,
                1,
                2,
                3,
                4,
                5,
                7,
                7,
                8,
                8
        );
        maleWeights = Arrays.asList(
                20.0,
                56.0,
                60.0,
                62.1,
                70.0,
                80.5,
                100.5,
                105.0,
                105.1,
                20000.4635656000
        );
        femaleWeightClasses = Arrays.asList(
                1,
                2,
                3,
                4,
                5,
                6,
                7
        );
        femaleWeights = Arrays.asList(
                48.0,
                48.1,
                55.0,
                60.0,
                68.9,
                70.0,
                100.0
        );
        lifters = new ArrayList<>();
        participants = new ArrayList<>();
        for (int i = 0; i < maleWeights.size(); i++) {
            Lifter l = mock(Lifter.class);
            when(l.getGender()).thenReturn(Lifter.Gender.MALE);
            when(l.getBodyWeight()).thenReturn(maleWeights.get(i));
            lifters.add(l);
            Participant p = mock(Participant.class);
            when(p.getLifter()).thenReturn(l);
            participants.add(p);
        }
        for (int i = 0; i < femaleWeights.size(); i++) {
            Lifter l = mock(Lifter.class);
            when(l.getGender()).thenReturn(Lifter.Gender.FEMALE);
            when(l.getBodyWeight()).thenReturn(femaleWeights.get(i));
            lifters.add(l);
            Participant p = mock(Participant.class);
            when(p.getLifter()).thenReturn(l);
            participants.add(p);
        }
    }

    @Test
    public void findWeightClassWithLifter() throws Exception {
        for (int i = 0; i < maleWeights.size(); i++) {
            Integer weightClass = WeightClass.findWeightClass(lifters.get(i));
            assertEquals(maleWeightClasses.get(i), weightClass);
        }
        for (int i = 0; i < femaleWeights.size(); i++) {
            Integer weightClass = WeightClass.findWeightClass(lifters.get(i+maleWeights.size()));
            assertEquals(femaleWeightClasses.get(i), weightClass);
        }
    }

    @Test
    public void findWeightClassWithParticipant() throws Exception {
        for (int i = 0; i < maleWeights.size(); i++) {
            Integer weightClass = WeightClass.findWeightClass(participants.get(i));
            assertEquals(maleWeightClasses.get(i), weightClass);
        }
        for (int i = 0; i < femaleWeights.size(); i++) {
            Integer weightClass = WeightClass.findWeightClass(participants.get(i+maleWeights.size()));
            assertEquals(femaleWeightClasses.get(i), weightClass);
        }
    }

}