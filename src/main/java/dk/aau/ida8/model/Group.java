package dk.aau.ida8.model;


import dk.aau.ida8.util.groupcomparators.CompetingComparator;
import dk.aau.ida8.util.groupcomparators.SinclairRankingComparator;
import dk.aau.ida8.util.groupcomparators.TotalWeightRankingComparator;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Transient
    private Comparator<Participant> groupComparator;

    /**
     * Empty constructor required by Hibernate.
     */
    public Group() {

    }

    /**
     * Creates a new Group object.
     *
     * @param competition    the competition within which the group is situated
     * @param participants   the participants within the group
     * @param comparatorType the type of grouping this group represents
     */
    public Group(Competition competition,
                  List<Participant> participants,
                  ComparatorType comparatorType) {
        this.competition = competition;
        this.participants = participants;
        this.comparatorType = comparatorType;
        createGroupComparator();
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
        return this.getParticipants().equals(g.getParticipants()) &&
                this.getComparatorType().equals(g.getComparatorType());
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

    /**
     * Gets an unsorted list of all participants.
     *
     * This should only be used where it is expressly desired that the
     * participants in this group not be sorted.
     *
     * @return an unsorted list of all participants in the group
     */
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
     * Creates a group comparator object for this group.
     *
     * The group comparator object is determined by checking which ComparatorType
     * is associated with this group.
     */
    private void createGroupComparator() {
        if (getComparatorType() == ComparatorType.SINCLAIR_RANKING) {
            this.groupComparator = new SinclairRankingComparator();
        } else if (getComparatorType() == ComparatorType.TOTAL_WEIGHT_RANKING) {
            this.groupComparator = new TotalWeightRankingComparator();
        } else if (getComparatorType() == ComparatorType.COMPETING) {
            this.groupComparator =  new CompetingComparator();
        } else {
            String msg = "unknown competition type: " + getComparatorType();
            throw new UnsupportedOperationException(msg);
        }
    }

    /**
     * Gets the group comparator for this group.
     *
     * The group comparator object is used to compare different participants
     * within this group, according to the comparison manner specified therein.
     * A Sinclair ranking comparator, for example, will compare participants
     * using their Sinclair score.
     *
     * @return the group comparator for this group
     */
    public Comparator<Participant> getGroupComparator() {
        if (groupComparator == null) {
          createGroupComparator();
        }
        return groupComparator;
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
     * Determines whether this group is a competing group.
     *
     * @return true if competing group, else false
     */
    public boolean isCompetingGroup() {
      return comparatorType == ComparatorType.COMPETING;
    }

    /**
     * Determines whether this group is a ranking group.
     *
     * @return true if ranking group, else false
     */
    public boolean isRankingGroup() {
      return comparatorType == ComparatorType.SINCLAIR_RANKING
          || comparatorType == ComparatorType.TOTAL_WEIGHT_RANKING;
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
