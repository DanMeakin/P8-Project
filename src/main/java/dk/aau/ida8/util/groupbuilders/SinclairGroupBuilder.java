package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Participant;

import java.util.Comparator;
import java.util.function.Function;

/**
 * This class defines the SinclairGroupBuilder. It is used to generate
 * groups for Sinclair competitions.
 *
 * In a Sinclair competition, ranking groups contain lifters of the same
 * gender. These are sorted, so that women are listed first and
 * men second.
 *
 * Competing groups are simply the ranking groups broken-down into smaller
 * sub-groups with no more than 10 members in each.
 */
public class SinclairGroupBuilder extends GroupBuilder {

    /**
     * Constructs a SinclairGroupBuilder instance.
     *
     * @param competition the competition for which to build groups
     */
    public SinclairGroupBuilder(Competition competition) {
        super(competition);
    }

    /**
     * Gets the ranking grouper for this builder.
     *
     * In Sinclair competitions, ranking groups contain participants of
     * the same gender. This method returns a function which
     * returns the participant's gender.
     *
     * @return function returning a participant's gender
     */
    @Override
    Function<Participant, Object> getRankingGrouper() {
        return Participant::getGender;
    }

    /**
     * Gets the ranking comparator type for this builder.
     *
     * The ranking comparator type for a total weight competition is the
     * {@code SINCLAIR_RANKING} comparator.
     *
     * @return the {@code SINCLAIR_RANKING} ComparatorType value
     */
    @Override
    Group.ComparatorType getRankingComparatorType() {
        return Group.ComparatorType.SINCLAIR_RANKING;
    }

    /**
     * Gets the ranking group comparator for this builder.
     *
     * The ranking groups in a Sinclair competition are sorted by gender.
     * This method returns a comparator which can
     * be used to compare Group objects on this basis.
     *
     * @return Group comparator which compares groups by gender
     */
    @Override
    Comparator<Group> getRankingGroupComparator() {
        return compareFirstByGender((g1, g2) -> 0);
    }

    /**
     * Gets the competing group comparator for this builder.
     *
     * The competing groups in a Sinclair competition are sorted by gender,
     * and then by starting snatch weight. This method returns a comparator
     * which can be used to compare Group objects on this basis.
     *
     * @return Group comparator which compares groups by gender and by
     *         starting snatch weight
     */
    @Override
    Comparator<Group> getCompetingGroupComparator() {
        return compareFirstByGender(
                (g1, g2) -> g1.getFirstParticipant().getStartingSnatchWeight() -
                            g2.getFirstParticipant().getStartingSnatchWeight()
        );
    }

    /**
     * Gets the maximum competing group size for this builder.
     *
     * No competing group can be larger than 10 participants in a Sinclair
     * competition. This method returns this value.
     *
     * @return 10, representing the maximum competing group size
     */
    @Override
    int getCompetingGroupMaxSize() {
        return 10;
    }
}