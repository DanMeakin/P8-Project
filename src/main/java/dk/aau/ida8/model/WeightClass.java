package dk.aau.ida8.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightClass {

    private static Map<Lifter.Gender, List<Integer>> weightClasses = new HashMap<>();

    private static List<Integer> maleClasses = Arrays.asList(56, 62, 69, 77, 85, 94, 105);
    private static List<Integer> femaleClasses = Arrays.asList(48, 53, 58, 63, 69, 75);

    private static void populateWeightClasses() {
        weightClasses.put(Lifter.Gender.MALE, maleClasses);
        weightClasses.put(Lifter.Gender.FEMALE, femaleClasses);
    }

    public static int findWeightClass(Participant p) {
        return findWeightClass(p.getLifter());
    }

    public static int findWeightClass(Lifter l) {
        populateWeightClasses();
        return (int) getWeightClasses().get(l.getGender()).stream()
                .filter(wc -> l.getBodyWeight() > wc)
                .count() + 1;
    }

    public static Map<Lifter.Gender, List<Integer>> getWeightClasses() {
        if (weightClasses.isEmpty()) {
            populateWeightClasses();
        }
        return weightClasses;
    }
}
