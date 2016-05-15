package dk.aau.ida8.model;


import dk.aau.ida8.model.groupComparators.CompetingComparator;
import dk.aau.ida8.model.groupComparators.SinclairRankingComparator;
import dk.aau.ida8.model.groupComparators.TotalWeightRankingComparator;
import dk.aau.ida8.util.Tuple;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents one group within a weightlifting competition.
 *
 * Once all participants are signed-up to a competition and weigh-in has been
 * completed, the {@link Competition} generates a series of groups to represent
 * the grouping of participants within the competition.
 *
 * There are two kinds of grouping within a competition:-
 *
 * <ol>
 *     <li>Competing groups; and</li>
 *     <li>Ranking groups.</li>
 * </ol>
 *
 * The former are used to order lifters within groups for the purpose of their
 * participation in the competition. Within the competition, each group carries-
 * out all of their lifts before the next group begins. This process repeats
 * until all groups have completed all lifts.
 *
 * The latter are used to determine the rankings of participants in the results
 * of a competition. Depending on the type of competition, there will be one or
 * more ranking groups for each competition. These are broken down by gender in
 * every case, with separate groupings for male and female participants, and
 * then may be broken down further into weight groups for that type of
 * competition.
 *
 * Each group contains methods for getting, sorting and ranking participants.
 * Where competing groups are concerned, methods for ranking relate only to the
 * order in which participants participate in lifts.
 */
@Entity
@Table(name="COMPETITION_GROUP")
public class Group {

    /**
     * Defines a series of comparator types, each representing the way in which
     * members of the group are compared.
     */
    public enum ComparatorType {
        COMPETING,
        SINCLAIR_RANKING,
        TOTAL_WEIGHT_RANKING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    /**
     * The list of Participants within a Group.
     */
    @ManyToMany(cascade = {CascadeType.ALL})
    private List<Participant> participants;

    private ComparatorType comparatorType;

    @ManyToOne
    private Competition competition;

    public Group() {

    }

    /**
     * Creates a new Group object.
     *
     * @param competition    the competition within which the group is situated
     * @param participants   the participants within the group
     * @param comparatorType the type of grouping this group represents
     */
    private Group(Competition competition,
                  List<Participant> participants,
                  ComparatorType comparatorType) {
        this.competition = competition;
        this.participants = participants;
        this.comparatorType = comparatorType;
    }

    /**
     * Creates a new Group object for ranking in a Sinclair competition.
     *
     * @param competition  the competition within which the group is situated
     * @param participants the participants within the group
     * @return             a group object for ranking in a Sinclair competition
     */
    public static Group sinclairRankingGroup(Competition competition,
                                             List<Participant> participants) {
        return new Group(competition, participants, ComparatorType.SINCLAIR_RANKING);
    }

    /**
     * Creates a new Group object for ranking in a total weight competition.
     *
     * @param competition  the competition within which the group is situated
     * @param participants the participants within the group
     * @return             a group object for ranking in a total weight competition
     */
    public static Group totalWeightRankingGroup(Competition competition,
                                                List<Participant> participants) {
        return new Group(competition, participants, ComparatorType.TOTAL_WEIGHT_RANKING);
    }

    /**
     * Creates a new competing Group object.
     *
     * @param competition  the competition within which the group is situated
     * @param participants the participants within the group
     * @return             a competing group object
     */
    public static Group competingGroup(Competition competition,
                                       List<Participant> participants) {
        return new Group(competition, participants, ComparatorType.COMPETING);
    }

    /**
     * Creates a series of ranking groups for a given competition.
     *
     * Ranking groups are used to determine the final scores within a
     * competition. This method splits participants into a series of groups
     * based on the type of competition: Sinclair or Total Weight.
     *
     * A Sinclair competition groups participants by gender.
     *
     * A Total Weight competition groups participants by gender and by weight
     * group.
     *
     * @param competition the competition for which to create groups
     * @return            a list of ranking groups generated from competition
     */
    public static List<Group> createRankingGroups(Competition competition) {
        Competition.CompetitionType ct = competition.getCompetitionType();
        if (ct.equals(Competition.CompetitionType.SINCLAIR)) {
            return createSinclairRankingGroups(competition);
        } else if (ct.equals(Competition.CompetitionType.TOTAL_WEIGHT)) {
            return createTotalWeightRankingGroups(competition);
        }
        String msg = "unknown competition type: " + ct;
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Creates a series of ranking groups for a Sinclair competition.
     *
     * A Sinclair competition groups participants by gender.
     *
     * @param competition the competition for which to create groups
     * @return            a list of ranking groups generated from competition
     */
    private static List<Group> createSinclairRankingGroups(Competition competition) {
        List<Participant> ps = new ArrayList<>(competition.getParticipants());
        Map<Lifter.Gender, List<Participant>> pGrp = ps.stream()
                .sorted((p1, p2) -> p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight())
                .collect(Collectors.groupingBy(Participant::getGender));
        List<Group> groups = new ArrayList<>();
        pGrp.values().forEach(grp -> groups.add(sinclairRankingGroup(competition, grp)));
        Collections.sort(groups, (g1, g2) -> {
            boolean g1Female = g1.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean g2Female = g2.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean oneFemale = g1Female ^ g2Female;
            if (oneFemale) {
                return g1Female ? -1 : 1;
            } else {
                return g1.getParticipants().get(0).getStartingSnatchWeight() -
                        g2.getParticipants().get(0).getStartingSnatchWeight();
            }
        });
        return groups;
    }

    /**
     * Creates a series of ranking groups for a total weight competition.
     *
     * A Total Weight competition groups participants by gender and by weight
     * group.

     * @param competition the competition for which to create groups
     * @return            a list of ranking groups generated from competition
     */
    private static List<Group> createTotalWeightRankingGroups(Competition competition) {
        List<Participant> ps = new ArrayList<>(competition.getParticipants());
        Map<Tuple<Lifter.Gender, Integer>, List<Participant>> pGrp = ps.stream()
                .sorted((p1, p2) -> p1.getStartingSnatchWeight() - p2.getStartingSnatchWeight())
                .collect(Collectors.groupingBy(p -> {
                    return new Tuple<>(p.getGender(), p.getWeightClass());
                }));
        List<Group> groups = new ArrayList<>();
        pGrp.values().forEach(grp -> groups.add(totalWeightRankingGroup(competition, grp)));
        Collections.sort(groups, (g1, g2) -> {
            boolean g1Female = g1.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean g2Female = g2.getGroupGender().equals(Lifter.Gender.FEMALE);
            boolean oneFemale = g1Female ^ g2Female;
            if (oneFemale) {
                return g1Female ? -1 : 1;
            } else {
                return (int)
                        (g1.getParticipants().get(0).getWeightClass() -
                        g2.getParticipants().get(0).getWeightClass());
            }
        });
        return groups;
    }

    /**
     * Creates a series of competing groups for a competition.
     *
     * Competing groups are generated from ranking groups, splitting each ranking
     * group into no more than 10 participants.
     *
     * @param competition the competition for which to create groups
     * @return            a list of competing groups generated from competition
     */
    public static List<Group> createCompetingGroups(Competition competition) {
        List<Group> rankingGroups = createRankingGroups(competition);
        return chunkGroups(competition, rankingGroups, 10);
    }

    /**
     * Determines whether this is equal to another object.
     *
     * This method checks that the other object is a Group, and that it is
     * equal according to the {@link #equals(Group) group equality} method.
     *
     * @param o the other object to compare for equality
     * @return  true if equal, else false
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Group && equals((Group) o);
    }

    /**
     * Determines whether this is equal to another group.
     *
     * Two groups are equal only if they contain the same participants, and they
     * share the same comparator.
     *
     * @param g the other group to compare to this
     * @return  true if equal, else false
     */
    public boolean equals(Group g) {
        List<Participant> l1 = this.getParticipants();
        List<Participant> l2 = g.getParticipants();
        ComparatorType c1 = this.getComparatorType();
        ComparatorType c2 = g.getComparatorType();
        return l1.equals(l2) && c1.equals(c2);
    }

    /**
     * Gets the first participant within the group.
     *
     * This will represent the participant currently in the lead for a ranking
     * group, or the participant next to lift in a competing group.
     *
     * @return the first participant within the group
     */
    public Participant getFirstParticipant() {
        return getParticipants().get(0);
    }

    /**
     * Gets a sorted list of all participants.
     *
     * @return sorted list of all participants in the group
     */
    public List<Participant> getParticipants() {
        sortParticipants();
        return participants;
    }

    public List<Participant> getUnsortedParticipants() {
        return participants;
    }

    /**
     * Determines whether the group contains a given participant.
     *
     * @param p participant to check for membership in group
     * @return  true if participant is a member of the group, else false
     */
    public boolean containsParticipant(Participant p) {
        return participants.contains(p);
    }

    /**
     * Counts the number of participants in group.
     *
     * @return number of participants in group
     */
    public int getParticipantsCount() {
        return participants.size();
    }

    /**
     * Sorts and returns the list of participants.
     *
     * The identity of the next participant is calculated based on which
     * participant has chosen the lowest weight to lift. If two or more lifters
     * are to be lifting the same weight, the next participant is determined
     * by taking the lowest of these lifters' ID#.
     */
    public void sortParticipants() {
        participants.sort(getGroupComparator());
    }

    /**
     * Gets the relevant group comparator object for this group.
     *
     * The group comparator object is determined by checking which ComparatorType
     * is associated with this group.
     *
     * @return group comparator object for this group
     */
    public Comparator<Participant> getGroupComparator() {
        if (getComparatorType() == ComparatorType.SINCLAIR_RANKING) {
            return new SinclairRankingComparator();
        } else if (getComparatorType() == ComparatorType.TOTAL_WEIGHT_RANKING) {
            return new TotalWeightRankingComparator();
        } else if (getComparatorType() == ComparatorType.COMPETING) {
            return new CompetingComparator();
        }
        String msg = "unknown competition type: " + getComparatorType();
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Determines the gender of the participants in this group.
     *
     * @return the gender of the participants in this group
     */
    public Lifter.Gender getGroupGender() {
        return this.getParticipants().get(0).getGender();
    }

    /**
     * Adds a participant to this group.
     *
     * @param p participant to add
     */
    public void addParticipant(Participant p){
        getParticipants().add(p);
    }

    /**
     * Gets the rank of a particular participant within this Group.
     *
     * The rank will relate either to the order of proceedings for a competing
     * group, or to the score of the participant within the group for a ranking
     * group.
     *
     * @param p                          the participant of whom to obtain
     *                                   the rank
     * @return                           the rank of the given participant
     * @throws InvalidParameterException if participant is not present within
     *                                   group
     */
    public int getRank(Participant p) throws InvalidParameterException {
        if (getParticipants().contains(p)) {
            return getRankings().get(p);
        } else {
            String msg = "participant " + p + " is not in this group";
            throw new InvalidParameterException(msg);
        }
    }

    /**
     * Determines the rankings of all participants within the group.
     *
     * Because participants may be ranked equally, this method groups all
     * participants using the encapsulated comparator, and then groups the
     * participants by "score". Rankings are contained within a Map indexed
     * by rank, with lists of participants for its values.
     *
     * In most cases, the list will contain only one value. However, where
     * participants are tied, a list will contain more elements.
     *
     * @return a map indexed by rank with value being a list of participants
     */
    private Map<Participant, Integer> getRankings() {
        List<Participant> ps = getParticipants();
        List<Integer> ranks = new ArrayList<>();
        ranks.add(1); // First value will always be 1
        for (int i = 1; i < ps.size(); i++) {
            if (getGroupComparator().compare(ps.get(i-1), ps.get(i)) == 0) {
                int prevRank = ranks.get(ranks.size()-1);
                ranks.add(prevRank);
            } else {
                ranks.add(i+1);
            }
        }
        Map<Participant, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < ranks.size(); i++) {
            rankMap.put(ps.get(i), ranks.get(i));
        }
        return rankMap;
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
    private static List<Group> chunkGroups(Competition c, List<Group> groups, int chunkSize) {
        List<Group> chunkedGroups = new ArrayList<>();
        for (Group g : groups) {
            for (int i = 0; i < g.getParticipantsCount(); i += chunkSize) {
                List<Participant> subList = new ArrayList<>(g.getParticipants().subList(
                                i,
                                Math.min(i+chunkSize, g.getParticipantsCount())
                        ));
                chunkedGroups.add(competingGroup(c, subList));
            }
        }
        return chunkedGroups;
    }

    /**
     * Gets the ID# of this group.
     *
     * Required for Hibernate.
     *
     * @return ID# of this group
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the comparator type applicable to this group.
     *
     * @return comparator type applicable to this group
     */
    public ComparatorType getComparatorType() {
        return comparatorType;
    }

    /**
     * Gets the competition to which this group belongs.
     *
     * @return competition to which this group belongs
     */
    public Competition getCompetition() {
        return competition;
    }
}
