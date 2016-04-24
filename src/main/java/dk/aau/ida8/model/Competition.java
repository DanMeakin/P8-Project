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
 * the scoring rules adopted. The participants, after weigh-in, are allocated
 * to groups
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

    /**
     * Creates an empty Competition object.
     *
     * An empty constructor is required by Hibernate.
     */
    public Competition(){
        this.participants = new ArrayList<>();
    }

    /**
     * Compares this against another object.
     *
     * Competition is equal to another object only where it is a Competition,
     * and the other object is equal to this as determined by the
     * {@link Competition#equals(Competition)} method.
     *
     * @param o the other object to compare this against
     * @return true if the other object is a competition and satisfies the
     *              {@link Competition#equals(Competition)} method
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Competition && equals((Competition) o);
    }

    /**
     * Compares this competition against another for equality.
     *
     * @param c the other competition to compare this against
     * @return true, if both competition have the same ID number, else false
     */
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
    private void allocateGroups() {
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

    /**
     * Gets a list of all participants signed-up to participant in this
     * competition.
     *
     * @return the list of all participants signed-up to participate in this
     *         competition
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Gets a list of all lifters participating in this competition.
     *
     * @return the list of all lifters participating in this competition
     */
    public List<Lifter> getLifters() {
        return getParticipants().stream()
                .map(Participant::getLifter)
                .collect(Collectors.toList());
    }

    /**
     * Gets the name of this competition.
     *
     * @return the name of this competition
     */
    public String getCompetitionName() {
        return competitionName;
    }

    /**
     * Gets the location at which this competition is taking place.
     *
     * @return the location at which this competition takes place
     */
    public Address getLocation() {
        return this.location;
    }

    /**
     * Gets the date of this competition.
     *
     * @return the date of this competition
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the date on which registration for this competition closes.
     *
     * @return the date on which registration closes
     */
    public Date getLastRegistrationDate() {
        return lastRegistrationDate;
    }

    /**
     * Gets the maximum number of participants permitted in this competition.
     *
     * @return the maximum number of participants for this competition
     */
    public int getMaxNumParticipants() {
        return maxNumParticipants;
    }

    /**
     * Gets the host club for this competition.
     *
     * @return the host club for this competition
     */
    public Club getHost() {
        return host;
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

    /**
     * Gets the ranking group containing the participants currently
     * competing in the competition.
     *
     * This returns an optional group: if the competition is complete, there
     * is no current ranking group and nothing is returned.
     *
     * @return the current ranking group, or nothing if the competition is
     *         complete
     */
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

    /**
     * Gets the list of ranking groups for this competition.
     *
     * Ranking groups are the divisions of participants based on the ranking
     * method chosen for this competition. For example, in a Sinclair
     * competition, participants are grouped into a maximum of two ranking
     * groups, one for each gender. In such a competition, this method returns
     * a list of up to two groups.
     *
     * @return the list of ranking groups for this competition
     */
    public List<Group> getRankingGroups() {
        return rankingGroups;
    }

    /**
     * Sets the list of ranking groups for this competition.
     *
     * @param rankingGroups the new list of ranking groups for this competition
     */
    private void setRankingGroups(List<Group> rankingGroups) {
        this.rankingGroups = rankingGroups;
    }

    /**
     * Gets the list of competing groups for this competition.
     *
     * Competing groups are the divisions of participants into groups for the
     * purpose of the competition proceedings. The grouping is done in
     * accordance with the methods contained in the {@link Group} class. This
     * will split participants into small groups which participate together in
     * undertaking their lifts.
     *
     * @return the list of ranking groups for this competition
     */
    public List<Group> getCompetingGroups() {
        return competingGroups;
    }

    /**
     * Sets the list of competing groups for this competition.
     *
     * @param competingGroups the new list of competing groups for this
     *                        competition
     */
    private void setCompetingGroups(List<Group> competingGroups) {
        this.competingGroups = competingGroups;
    }

    /**
     * Lists all remaining available start numbers in the competition.
     *
     * Start numbers are allocated randomly from a range of numbers from 1 up
     * to the maximum number of participants. The same number cannot be
     * allocated twice. This method generates a range of numbers up to max
     * participants, and excludes those already allocated. It then returns this
     * list.
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

    /**
     * Finishes the weigh-in stage of the competition.
     *
     * This method should be called when the weigh-in stage of the competition
     * has been completed by the user. Any signed-up participants should be
     * removed from the competition if they have not yet been checked-in by the
     * time weigh-in is finished.
     */
    public void finishWeighIn() {
        getParticipants().stream()
                .filter(Participant::isNotCheckedIn)
                .forEach(this::removeParticipant);
        allocateGroups();
    }
}
