package dk.aau.ida8.model;


import dk.aau.ida8.model.groupComparators.CompetingComparator;
import dk.aau.ida8.model.groupComparators.SinclairRankingComparator;
import dk.aau.ida8.model.groupComparators.TotalWeightRankingComparator;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="COMPETITION_GROUP")
public class Group {

    /**
     * Defines an immutable pair of values. To be used as the key to a map
     * created for grouping participants.
     */
    static class Tuple<T, U> {
        private final T fst;
        private final U snd;

        public Tuple(T fst, U snd) {
            this.fst = fst;
            this.snd = snd;
        }

        public T getFst() {
            return fst;
        }

        public U getSnd() {
            return snd;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Tuple && equals((Tuple) o);
        }

        public boolean equals(Tuple o) {
            return this.getFst().equals(o.getFst()) &&
                    this.getSnd().equals(o.getSnd());
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }

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

    private Group(Competition competition,
                  List<Participant> participants,
                  ComparatorType comparatorType) {
        this.competition = competition;
        this.participants = participants;
        this.comparatorType = comparatorType;
    }

    public static Group sinclairRankingGroup(Competition competition,
                                             List<Participant> participants) {
        return new Group(competition, participants, ComparatorType.SINCLAIR_RANKING);
    }

    public static Group totalWeightRankingGroup(Competition competition,
                                                List<Participant> participants) {
        return new Group(competition, participants, ComparatorType.TOTAL_WEIGHT_RANKING);
    }

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
     * A Sinclair competition groups participants by gender, and then splits
     * each gender group into sub-groups of no more than 10 participants.
     *
     * A Total Weight competition groups participants by gender and by weight
     * group, and then splits each of these groups into sub-groups of no more
     * than 10 participants.
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

    public static List<Group> createCompetingGroups(Competition competition) {
        List<Group> rankingGroups = createRankingGroups(competition);
        return chunkGroups(competition, rankingGroups, 10);
    }

    @Override
    public String toString() {
        String s = "Group: " + getParticipantsCount() + " " +
                getParticipants().get(0).getGender() + " participants (";
        for (Participant p : getParticipants()) {
            s += p.toString() + ", ";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Group && equals((Group) o);
    }

    public boolean equals(Group g) {
        List<Participant> l1 = this.getParticipants();
        List<Participant> l2 = g.getParticipants();
        return l1.equals(l2);
    }

    public Participant getFirstParticipant() {
        return getParticipants().get(0);
    }

    public List<Participant> getParticipants() {
        sortParticipants();
        return participants;
    }

    public List<Participant> getUnsortedParticipants() {
        return participants;
    }

    public boolean containsParticipant(Participant p) {
        return participants.contains(p);
    }

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

    public Lifter.Gender getGroupGender() {
        return this.getParticipants().get(0).getGender();
    }

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
    public int getRank(Participant p) {
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
    protected static List<Group> chunkGroups(Competition c, List<Group> groups, int chunkSize) {
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

    public long getId() {
        return id;
    }

    public ComparatorType getComparatorType() {
        return comparatorType;
    }

    public Competition getCompetition() {
        return competition;
    }
}
