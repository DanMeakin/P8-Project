package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.model.Participant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This abstract class defines a GroupBuilder. It is inherited by classes
 * which build groups for the purpose of competitions.
 *
 * The purpose of this builder is to generate groupings for ranking purposes,
 * and for competing purposes, within a competition. Ranking groups are used
 * to determine the placement of competitors in the results table at the end
 * of the competition. Different competitions will require different ranking
 * groups to be created depending on their rules.
 *
 * Competing groups are used to determine the order and sequence in which
 * participants compete within a competition. The interface defines a default
 * method in which the competing groups are based on breaking-down the ranking
 * groups into sub-groups of up to 10 participants. This can be overridden by
 * implementing classes.
 *
 * The GroupBuilder requires that inheriting classes implement four methods:
 *
 * <ol>
 *     <li>{@link #getRankingGrouper()};</li>
 *     <li>{@link #getRankingComparatorType()};</li>
 *     <li>{@link #getRankingGroupComparator()}; and</li>
 *     <li>{@link #getCompetingGroupMaxSize()}.</li>
 * </ol>
 *
 * The first three relate to the (default) method by which ranking groups
 * are created. See {@link #createRankingGroups()} for details.
 *
 * The last method sets the maximum size for a competing group. These groups
 * will be no larger than this size.
 */
public abstract class GroupBuilder {

    private final Competition competition;

    /**
     * Gets the ranking grouper for this builder.
     *
     * The ranking grouper is used to group together participants into
     * ranking groups. The function is used to group participants who share
     * the same return value when passed as argument to the function.
     *
     * For example, if ranking grouper was {@code Participant::getGender},
     * then this will be used in {@link #createRankingGroups()} to group
     * together participants by gender, placing all participants of the
     * same gender into one group.
     *
     * @return ranking grouper used to group participants into ranking groups
     */
    abstract Function<Participant, Object> getRankingGrouper();

    /**
     * Gets the ranking comparator type for this builder.
     *
     * The ranking comparator type is used in Group creation. It marks the
     * type of group which is created by {@link #createRankingGroups()}. This
     * is used by the group to carry out in-group sorting and ranking.
     *
     * @return ranking comparator type for this builder
     */
    abstract Group.ComparatorType getRankingComparatorType();

    /**
     * Gets the ranking group comparator for this builder.
     *
     * The ranking group comparator is used to sort the list of ranking
     * groups created by {@link #createRankingGroups()} after they have been
     * generated.
     *
     * For example, a comparator which sorted groups in ascending order based
     * on the starting snatch weight of the competitors within the group
     * could be defined as
     * {@code (g1, g2) -> g1.getParticipants().get(0).getStartingSnatchWeight()
     * - g2.getParticipants().get(0).getStartingSnatchWeight()}.
     *
     * @return ranking group comparator for this builder
     */
    abstract Comparator<Group> getRankingGroupComparator();

    /**
     * Gets the maximum competing group size for this builder.
     *
     * The size of competing groups requires to be limited. This method returns
     * the value of this limit for this builder.
     *
     * @return maximum competing group size for this builder
     */
    abstract int getCompetingGroupMaxSize();

    /**
     * Constructs a GroupBuilder instance.
     *
     * @param competition the competition for which to build groups
     */
    public GroupBuilder(Competition competition) {
        this.competition = competition;
    }

    /**
     * Gets the competition to which this builder relates.
     *
     * @return competition to which this relates
     */
    private Competition getCompetition() {
        return this.competition;
    }

    /**
     * Generates a list of groups to be used for ranking in competitions.
     *
     * This method creates a pair of streams operating over the participants
     * within the associated competition. The first stream sorts the list of
     * participants, and then groups them using the {@link #getRankingGrouper()
     * ranking grouper}.
     *
     * The second stream takes the values from the map created using that
     * grouping, creates a Group instance for each sub-list of Participants
     * and then sorts according to the {@link #getRankingGroupComparator()
     * ranking group comparator}.
     *
     * @return list of ranking groups
     */
    public List<Group> createRankingGroups() {
        return getCompetition().getParticipants()
                // Create the first stream, sorting by starting snatch weight
                // and then grouping using rankingGrouper
                .stream()
                .sorted((p1, p2) -> p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight())
                .collect(Collectors.groupingBy(getRankingGrouper()))
                .values()
                // Create the second stream, creating Group objects for each
                // group of participants, and then sorting
                .stream()
                .map(grp -> new Group(getCompetition(), grp, getRankingComparatorType()))
                .sorted(getRankingGroupComparator())
                .collect(Collectors.toList());
    }

    /**
     * Generates a list of groups to be used for competing in competitions.
     *
     * This method creates a list of competing groups, in which no group
     * contains more than {@link #getCompetingGroupMaxSize()} participants.
     * The procedure is as follows:-
     *
     * <ol>
     *     <li>
     *         Take the {@link #createRankingGroups() ranking groups} created
     *         elsewhere;
     *     </li>
     *     <li>
     *         {@link #chunkParticipants(List) Chunk} the participants within
     *         each group;
     *     </li>
     *     <li>
     *         Concatenate the results of the chunk process (this will result
     *         in a flat stream containing {@code List<Participant>} objects;
     *         and
     *     </li>
     *     <li>
     *         Create a new competing group from each chunked set of
     *         participants.
     *     </li>
     * </ol>
     *
     * @return list of competing groups for the competition associated with
     *         this
     */
    public List<Group> createCompetingGroups() {
        return createRankingGroups()
                .stream()
                .flatMap(g -> chunkParticipants(g.getParticipants()))
                .map(ps -> new Group(getCompetition(), ps, Group.ComparatorType.COMPETING))
                .collect(Collectors.toList());
    };

    /**
     * Creates a comparator which compares two groups first by gender, and then
     * by a second comparator.
     *
     * @param secondComparator the comparator to use after comparing by gender
     * @return a comparator which compares two groups by gender and then by
     *         another factor
     */
    protected static Comparator<Group> compareFirstByGender(Comparator<Group> secondComparator) {
        return (g1, g2) -> {
            boolean g1Female = g1.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean g2Female = g2.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean oneFemale = g1Female ^ g2Female;
            if (oneFemale) {
                return g1Female ? -1 : 1;
            } else {
                return secondComparator.compare(g1, g2);
            }
        };
    }

    /**
     * Divides a list of Groups of Participants into new Groupings of no larger
     * than a certain size.
     *
     * This method attempts to create groups as similarly sized as possible.
     * The groups generated by this method will all be size n or n + 1,
     * where n is equal to the number of participants divided by the number
     * of groups, rounded down.
     *
     * The number of groups is determined by dividing the number of
     * participants by the maximum competing group size, and rounding up.
     *
     * @param participants the list of participants to sub-divide
     * @return             a stream containing lists of participants each no
     *                     larger than {@link #getCompetingGroupMaxSize()}.
     */
    private Stream<List<Participant>> chunkParticipants(List<Participant> participants) {
        List<List<Participant>> result = new ArrayList<>();
        int numGroups = (int) Math.ceil(
                (double) participants.size() / getCompetingGroupMaxSize()
        );
        for (int i = 0, j = 0; i < numGroups; i++) {
            int groupSize = (int) Math.floor(participants.size() / numGroups);
            if (i < participants.size() % numGroups) {
                groupSize++;
            }
            List<Participant> subList = participants.subList(
                    j, j + groupSize
            );
            result.add(subList);
            j += groupSize;
        }
        return result.stream();
    }
}