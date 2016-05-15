package dk.aau.ida8.util;

import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.model.Participant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to determine the weight class of a given lifter within a
 * total weight competition.
 *
 * Within a total weight competition, participants compete within weight group
 * categories. They must be allocated to these categories following weigh-in.
 * This class provides a number of static methods for determining which weight
 * class a participant is in.
 *
 */
public class WeightClass {

    private static Map<Lifter.Gender, List<Integer>> weightClasses = new HashMap<>();

    private static List<Integer> maleClasses = Arrays.asList(56, 62, 69, 77, 85, 94, 105);
    private static List<Integer> femaleClasses = Arrays.asList(48, 53, 58, 63, 69, 75);

    /**
     * Finds the weight class for a given participant.
     *
     * This method extracts the lifter underlying a participant, and passes this
     * to the {@link #findWeightClass(Lifter)} method. See that method for more
     * details.
     *
     * @param p the participant for whom to find the weight class
     * @return  the weight class number for this participant
     */
    public static int findWeightClass(Participant p) {
        return findWeightClass(p.getLifter());
    }

    /**
     * Find the weight class for a given lifter.
     *
     * Each gender has a number of weight classes associated with it, based on
     * weight bandings. This method calculates which banding a lifter is in by
     * filtering the list of weight categories to select only those for which
     * the given lifter has a body weight higher than. This is then incremented
     * by 1, and the result is the weight category for this lifter.
     *
     * This functionality is based on the fact that the weight bandings listed
     * are for up to and including that weight. Thus, a lifter whose weight is
     * lower than that listed in Group 3 will be lighter than the weight listed
     * for Group 3, but heavier than that for Group 2. Filtering the list will
     * result in two entries being returned, with one needed to be added to get
     * the result of Group 3.
     *
     * @param l the lifter for whom to find the weight class
     * @return  the weight class number for this participant
     */
    public static int findWeightClass(Lifter l) {
        return (int) getWeightClasses().get(l.getGender()).stream()
                .filter(wc -> l.getBodyWeight() > wc)
                .count() + 1;
    }

    /**
     * Gets the Map of weight classes.
     *
     * Generates this map if not already populated.
     *
     * @return map of weight classes indexed by gender
     */
    private static Map<Lifter.Gender, List<Integer>> getWeightClasses() {
        if (weightClasses.isEmpty()) {
            populateWeightClasses();
        }
        return weightClasses;
    }

    /**
     * Populates the Map of weight classes.
     */
    private static void populateWeightClasses() {
        weightClasses.put(Lifter.Gender.MALE, maleClasses);
        weightClasses.put(Lifter.Gender.FEMALE, femaleClasses);
    }
}
