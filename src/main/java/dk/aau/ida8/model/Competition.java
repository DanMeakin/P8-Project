package dk.aau.ida8.model;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents one weightlifting competition.
 *
 * Each competition is hosted by a particular weightlifting club at a venue of
 * the club's choosing, and on a set date.
 *
 * The way in which a weightlifting competition is scored varies depending on
 * the scoring rules adopted. These rules are encapsulated within a
 * {@link ScoreStrategy, ScoreStrategy} object associated with a Competition
 * instance.
 *
 * After a competition is created, weightlifters sign-up to participate, with
 * each lifter's participation encapsulated and stored within a
 * {@link Participant Participant} instance.
 */
@Entity
public class Competition {

    public enum CompetitionType {
        SINCLAIR,
        TOTAL_WEIGHT
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Participant> participants;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Group> competingGroups;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Group> rankingGroups;

    private String competitionName;
    private CompetitionType competitionType;
    private int maxNumParticipants;

    public long getId() {
        return id;
    }

    @DateTimeFormat(pattern = "dd-mm-yyyy HH:mm")
    private Date date;

    @DateTimeFormat(pattern = "dd-mm-yyyy HH:mm")
    private Date lastRegistrationDate;

    @ManyToOne
    private Address location;

    @ManyToOne
    private Club host;

    /**
     * Creates a new Competition object.
     *
     * A competition requires a title, a host club, a location, a competition
     * type and a date. These are required parameters for the creation of a
     * Competition.
     *
     * @param competitionName the title/name of the competition
     * @param host            the club hosting the competition
     * @param location        the venue at which the competition takes place
     * @param competitionType the type of the competition, i.e. Sinclair or
     *                        total weight
     * @param date            the date on which the competition is to take place
     */
    public Competition(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants) {
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.location = location;
        this.date = date;
        this.lastRegistrationDate = lastRegistrationDate;
        this.maxNumParticipants = maxNumParticipants;
        this.host = host;
        this.participants = new ArrayList<>();
    }

    public Competition(){
        this.participants = new ArrayList<>();
    }

    public boolean equals(Object o) {
        if (o instanceof Competition) {
            return equals((Competition) o);
        } else {
            return false;
        }
    }

    public boolean equals(Competition c) {
        return getId() == c.getId();
    }

    /**
     * Adds a new participant to the competition.
     *
     * The participation of a lifter in a competition is encapsulated within a
     * {@link Participant Participant} object. This method accepts a lifter
     * instance and then creates and aggregates the required Participant
     * object to the competition.
     *
     * @param lifter the lifter to add to the competition
     */
    public void addParticipant(Lifter lifter, int startingWeight){
        Participant p = new Participant(lifter, this, startingWeight);
        addParticipant(p);
    }

    /**
     * Adds a new participant to the competition.
     *
     * @param p the participant to add to the competition
     */
    public void addParticipant(Participant p) {
        participants.add(p);
    }

    /**
     * Removes a participant from the list of participants.
     *
     * @param lifter who is to be removed
     */
    public void removeParticipant(Lifter lifter){
        Participant p = selectParticipantByLifter(lifter);
        removeParticipant(p);
    }

    /**
     * Removes a participant from the participants list.
     *
     * @param participant the participant object to remove
     */
    private void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    /**
     * Gets the participation object for the given lifter.
     *
     * @param lifter the lifter for which to obtain the participation
     *               instance
     * @return participation instance for the passed lifter
     */
    public Participant selectParticipantByLifter(Lifter lifter) {
        List<Participant> ps = getParticipants().stream()
                .filter(p -> p.getLifter().equals(lifter))
                .collect(Collectors.toList());
        return ps.get(0);
    }

    /**
     * Allocates participants to competing and ranking groups.
     *
     * The competing and ranking groups are used to ensure the proper order
     * of the competition, and to determine the winners of the competition
     * after completion, respectively.
     */
    public void allocateGroups() {
        setRankingGroups(Group.createRankingGroups(this));
        setCompetingGroups(Group.createCompetingGroups(this));
    };

    /**
     * Finds the participant who is to carry out a lift next. If the competing
     * stage of the competition is complete, no value is returned.
     *
     * @return the participant next to left
     */
    public Optional<Participant> currentParticipant() {
        Optional<Group> currentGroup = getCurrentCompetingGroup();
        if (currentGroup.isPresent()) {
            return Optional.of(currentGroup.get().getFirstParticipant());
        } else {
            return Optional.empty();
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Lifter> getLifters() {
        return getParticipants().stream()
                .map(Participant::getLifter)
                .collect(Collectors.toList());
    }


    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Address getLocation() {
        return this.location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLastRegistrationDate() {
        return lastRegistrationDate;
    }

    public void setLastRegistrationDate(Date lastRegistrationDate) {
        this.lastRegistrationDate = lastRegistrationDate;
    }

    public int getMaxNumParticipants() {
        return maxNumParticipants;
    }

    public void setMaxNumParticipants(int maxNumParticipants) {
        this.maxNumParticipants = maxNumParticipants;
    }

    public Club getHost() {
        return host;
    }

    public void setHost(Club host) {
        this.host = host;
    }

    /**
     * Calculates a specific participant's rank within the competition.
     *
     * @param participant                the participant for whom the ranking
     *                                   is being calculated
     * @return                           the passed participant's rank
     * @throws InvalidParameterException if participant cannot be found within
     *                                   any ranking group
     */
    public int getRank(Participant participant){
        for (Group g : getRankingGroups()) {
            if (g.containsParticipant(participant)) {
                return g.getRank(participant);
            }
        }
        String msg = "unable to find Participant within any ranking group";
        throw new InvalidParameterException(msg);
    }

    /**
     * Gets the competing group currently competing in the competition.
     *
     * This returns an optional group: if the competition is complete, there
     * is no currently competing group and nothing is returned.
     *
     * @return the group currently competing, or nothing if the competition is
     *         complete
     */
    public Optional<Group> getCurrentCompetingGroup() {
        for (Group g : getCompetingGroups()) {
            for (Participant p : g.getParticipants()) {
                if (!p.allLiftsComplete()) {
                    return Optional.of(g);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Group> getCurrentRankingGroup() {
        for (Group g : getRankingGroups()) {
            for (Participant p : g.getParticipants()) {
                if (!p.allLiftsComplete()) {
                    return Optional.of(g);
                }
            }
        }
        return Optional.empty();
    }

    public List<Group> getRankingGroups() {
        return rankingGroups;
    }

    protected void setRankingGroups(List<Group> rankingGroups) {
        this.rankingGroups = rankingGroups;
    }

    public List<Group> getCompetingGroups() {
        return competingGroups;
    }

    protected void setCompetingGroups(List<Group> competingGroups) {
        this.competingGroups = competingGroups;
    }

    /**
     * Lists all remaining available start numbers in the competition.
     *
     * @return list of all available start numbers
     */
    public List<Integer> availableStartNumbers() {
        List<Integer> remainingNumbers = new ArrayList<>();
        IntStream.range(1, getMaxNumParticipants())
                .forEach(remainingNumbers::add);
        getParticipants()
                .forEach(p -> remainingNumbers.remove((Integer) p.getStartNumber()));
        return remainingNumbers;
    }
}
