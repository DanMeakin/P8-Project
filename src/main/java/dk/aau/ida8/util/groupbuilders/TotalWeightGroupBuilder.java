package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Participant;
import dk.aau.ida8.util.Tuple;

import java.util.Comparator;
import java.util.function.Function;

/**
 * This class defines the TotalWeightGroupBuilder. It is used to generate
 * groups for total weight competitions.
 *
 * In a total weight competition, ranking groups contain lifters with the same
 * gender and in the same weight class. These are then sorted by gender, and
 * then by weight class, so that women are listed first, from lowest to highest
 * weight class, and then men from lowest to highest weight class.
 *
 * Competing groups are simply the ranking groups broken-down into smaller
 * sub-groups with no more than 10 members in each.
 */
public class TotalWeightGroupBuilder extends GroupBuilder {

    /**
     * Constructs a TotalWeightGroupBuilder instance.
     *
     * @param competition the competition for which to build groups
     */
    public TotalWeightGroupBuilder(Competition competition) {
        super(competition);
    }

    /**
     * Gets the ranking grouper for this builder.
     *
     * In total weight competitions, ranking groups contain participants of
     * the same gender and weight class. This method returns a function which
     * returns a tuple of a participant's gender and weight class.
     *
     * @return function returning a tuple containing a participant's gender
     *         and weight class
     */
    @Override
    Function<Participant, Object> getRankingGrouper() {
        return p -> new Tuple<>(p.getGender(), p.getWeightClass());
    }

    /**
     * Gets the ranking comparator type for this builder.
     *
     * The ranking comparator type for a total weight competition is the
     * {@code TOTAL_WEIGHT_RANKING} comparator.
     *
     * @return the {@code TOTAL_WEIGHT_RANKING} ComparatorType value
     */
    @Override
    Group.ComparatorType getRankingComparatorType() {
        return Group.ComparatorType.TOTAL_WEIGHT_RANKING;
    }

    /**
     * Gets the ranking group comparator for this builder.
     *
     * The ranking groups in a total weight competition are sorted by gender
     * and then by weight class. This method returns a comparator which can
     * be used to compare Group objects on this basis. See
     * {@link #secondaryGroupingComparator()} for details of the secondary
     * comparator used alongside gender.
     *
     * @return Group comparator which compares groups by gender and weight
     *         class
     */
    @Override
    Comparator<Group> getRankingGroupComparator() {
        return compareFirstByGender(secondaryGroupingComparator());
    }

    /**
     * Gets the competing group comparator for this builder.
     *
     * The competing groups in a total weight competition are sorted by
     * gender, then by weight class, and then by starting snatch weight.
     * This method returns a comparator which can be used to compare Group
     * objects on this basis.
     *
     * @return Group comparator which compares groups by gender, by
     *         weight class and by starting snatch weight
     */
    @Override
    Comparator<Group> getCompetingGroupComparator() {
        return compareFirstByGender(secondaryGroupingComparator());
    }
    /**
     * Gets the maximum competing group size for this builder.
     *
     * No competing group can be larger than 10 participants in a total weight
     * competition. This method returns this value.
     *
     * @return 10, representing the maximum competing group size
     */
    @Override
    int getCompetingGroupMaxSize() {
        return 10;
    }

    /**
     * Defines a secondary grouping comparator used to compare groups.
     *
     * This comparator can be used to sort groups based on the weight class
     * of the participants within the group.
     *
     * @return comparator which sorts by weight class
     */
    private Comparator<Group> secondaryGroupingComparator() {
        return (g1, g2) -> {
            int g1WC = (int) g1.getParticipants().get(0).getWeightClass();
            int g2WC = (int) g2.getParticipants().get(0).getWeightClass();
            int g1SS = g1.getParticipants().get(0).getStartingSnatchWeight();
            int g2SS = g2.getParticipants().get(0).getStartingSnatchWeight();
            if (g1WC - g2WC != 0) {
                return g1WC - g2WC;
            } else {
                return g1SS - g2SS;
            }
        };
    }
}