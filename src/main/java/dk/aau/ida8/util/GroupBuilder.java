package dk.aau.ida8.util;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Lifter;
import dk.aau.ida8.model.Participant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dk.aau.ida8.model.Group.competingGroup;

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
 */
public abstract class GroupBuilder {

    public GroupBuilder(Competition competition) {
        this.competition = competition;
    }

    /**
     * Contains the competition for which to build groups.
     */
    private Competition competition;

    /**
     * Gets the competition to which this builder relates.
     *
     * @return competition to which this relates
     */
    public Competition getCompetition() {
        return this.competition;
    }

    /**
     * Generates a list of groups to be used for ranking in competitions.
     *
     * @return list of ranking groups
     */
    public abstract List<Group> createRankingGroups();

    /**
     * Generates a list of groups to be used for competing in competitions.
     *
     * @return list of competing groups
     */
    public List<Group> createCompetingGroups() {
        List<Group> rankingGroups = createRankingGroups();
        return chunkGroups(rankingGroups, 10);
    };

    /**
     * Groups participants into a list of lists.
     *
     * @param grouper
     * @return
     */
    protected List<List<Participant>> groupParticipants(Function<Participant, Object> grouper) {
        List<Participant> ps = new ArrayList<>(getCompetition().getParticipants());
        Map<Object, List<Participant>> pGrp = ps.stream()
                .sorted((p1, p2) -> p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight())
                .collect(Collectors.groupingBy(grouper));
        return new ArrayList<>(pGrp.values());
    }

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
     * @param groups    the original groups to sub-divide
     * @param chunkSize the maximum size of the new groups
     * @return          a list of groups sub-divided to no more than a certain
     *                  size
     */
    private List<Group> chunkGroups(List<Group> groups, int chunkSize) {
        List<Group> chunkedGroups = new ArrayList<>();
        for (Group g : groups) {
            for (int i = 0; i < g.getParticipantsCount(); i += chunkSize) {
                List<Participant> subList = new ArrayList<>(g.getParticipants().subList(
                                i,
                                Math.min(i+chunkSize, g.getParticipantsCount())
                        ));
                chunkedGroups.add(competingGroup(getCompetition(), subList));
            }
        }
        return chunkedGroups;
    }
}