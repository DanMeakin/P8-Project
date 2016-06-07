package dk.aau.ida8.model;

import dk.aau.ida8.util.groupbuilders.GroupBuilder;
import dk.aau.ida8.util.groupbuilders.SinclairGroupBuilder;
import dk.aau.ida8.util.groupbuilders.TotalWeightGroupBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents one weightlifting competition.
 *
 * Each competition is hosted by a particular weightlifting club at a venue of
 * the club's choosing, and on a set competitionDate.
 *
 * There are essentially five stages to a competition:-
 *
 * <ol>
 *     <li>Creation & open for sign-up;</li>
 *     <li>Closed for sign-up, awaiting competition competitionDate;</li>
 *     <li>The weigh-in;</li>
 *     <li>The tournament itself; and</li>
 *     <li>Announcement of results.</li>
 * </ol>
 *
 * The stage of a competition is dependent on the values contained within it.
 * A competition is at the first stage after creation and until after the
 * {@link #lastRegistrationDate} is reached. It is at stage two after that
 * date and before the {@link #competitionDate} has been reached.
 *
 * It is at stage three on the day of the competition. At this point, it is
 * possible to {@link Participant#setWeighedIn(boolean) check-in} participants
 * to the competition. Once weight-in ends, the user will
 * {@link #finishWeighIn() finish weight-in} and the tournament can begin.
 *
 * The tournament proceeds by registering lifts by lifters. Once this is
 * complete, the results can be announced.
 *
 * The way in which a weightlifting competition is scored varies depending on
 * the scoring rules adopted. The participants, after weigh-in, are allocated
 * to groups for competing and ranking purposes. The competition proceeds by
 * having each competing group carry-out all of their lifts in turn, until all
 * participants have completed their lifts.
 *
 * The ranking groups are then used to determine the winners within each
 * particular division of the competition. For example, in a Sinclair
 * competition, participants are divided into two groups based on gender. Then,
 * the winners are announced for each of these gender groups. In a total weight
 * competition, participants are divided into multiple groups based on both
 * gender and weight class. Winners are announced for each of these
 * weight/gender groupings.
 *
 * After a competition is created, weightlifters sign-up to participate, with
 * each lifter's participation encapsulated and stored within a
 * {@link Participant Participant} instance.
 */
@Entity
public class Competition {

    /**
     * Defines an enumeration representing the type of a competition.
     */
    public enum CompetitionType {
        SINCLAIR("Sinclair"),
        TOTAL_WEIGHT("Total weight");

        private final String name;

        private CompetitionType(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public CompetitionType getCompetitionType() {
        return competitionType;
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "competition_id")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "competition_id")
    private List<Group> competingGroups = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "competition_id")
    private List<Group> rankingGroups = new ArrayList<>();

    private String competitionName;
    private CompetitionType competitionType;
    private int maxNumParticipants;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date competitionDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date lastRegistrationDate;

    @ManyToOne
    private Address location;

    @ManyToOne
    private Club host;

    /**
     * Contains the groupBuilder used to generate ranking and competing groups
     * for this competition.
     */
    @Transient
    private GroupBuilder groupBuilder;

    /**
     * Creates a new Competition object.
     *
     * A competition requires a title, a host club, a location, a competition
     * type and a competitionDate. These are required parameters for the creation of a
     * Competition.
     *
     * @param competitionName      the title/name of the competition
     * @param host                 the club hosting the competition
     * @param location             the venue at which the competition takes
     *                             place
     * @param competitionType      the type of the competition, i.e. Sinclair
     *                             or total weight
     * @param competitionDate      the date on which the competition is to take
     *                             place
     * @param lastRegistrationDate the date on which sign-up closes
     * @param maxNumParticipants   the maximum number of participants permitted
     *                             in this competition
     */
    public Competition(String competitionName,
                       Club host,
                       Address location,
                       CompetitionType competitionType,
                       Date competitionDate,
                       Date lastRegistrationDate,
                       int maxNumParticipants) {
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.location = location;
        this.competitionDate = competitionDate;
        this.lastRegistrationDate = lastRegistrationDate;
        this.maxNumParticipants = maxNumParticipants;
        this.host = host;
        createGroupBuilder();
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

    /***********
     * SETTERS *
     ***********/

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public void setHost(Club host) {
        this.host = host;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public void setCompetitionDate(Date competitionDate) {
        this.competitionDate = competitionDate;
    }

    public void setLastRegistrationDate(Date lastRegistrationDate) {
        this.lastRegistrationDate = lastRegistrationDate;
    }

    public void setMaxNumParticipants(int maxNumParticipants) {
        this.maxNumParticipants = maxNumParticipants;
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
     * Sets the list of competing groups for this competition.
     *
     * @param competingGroups the new list of competing groups for this
     *                        competition
     */
    private void setCompetingGroups(List<Group> competingGroups) {
        this.competingGroups = competingGroups;
    }

    /******************
     * END OF SETTERS *
     ******************/

    /***********
     * GETTERS *
     ***********/

    /**
     * Gets the ID# of this competition.
     *
     * @return the ID# of this competition
     */
    public long getId() {
        return id;
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
     * @return the competitionDate of this competition
     */
    public Date getCompetitionDate() {
        return competitionDate;
    }

    /**
     * Gets the date on which registration for this competition closes.
     *
     * @return the competitionDate on which registration closes
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
     * Gets the name of the club hosting this competition.
     *
     * @return the name of the hosting club
     */
    public String getHostName() {
        if (host == null) {
            return "";
        } else {
            return host.getName();
        }
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
        return rankingGroups.stream
                .filter(Group::isRankingGroup)
                .collect(Collectors.toList());
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
        return competingGroups.stream
                .filter(Group::isCompetingGroup)
                .collect(Collectors.toList());
    }

    /******************
     * END OF GETTERS *
     ******************/

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
    public void addParticipant(Lifter lifter) {
        Participant p = new Participant(lifter, this);
        addParticipant(p);
    }

    /**
     * Adds a new participant to the competition.
     *
     * @param p the participant to add to the competition
     */
    void addParticipant(Participant p) {
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
    void removeParticipant(Participant participant) {
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
     * Creates and stores a GroupBuilder within this competition.
     *
     * The GroupBuilder is used to generate the participant groups required
     * for this competition.
     *
     * @throws UnsupportedOperationException if competition type is unrecognised
     */
    private void createGroupBuilder() {
        if (getCompetitionType() == CompetitionType.SINCLAIR) {
            this.groupBuilder = new SinclairGroupBuilder(this);
        } else if (getCompetitionType() == CompetitionType.TOTAL_WEIGHT) {
            this.groupBuilder = new TotalWeightGroupBuilder(this);
        } else {
            String msg = "unrecognised CompetitionType: " + getCompetitionType();
            throw new UnsupportedOperationException(msg);
        }
    }

    /**
     * Gets the groupBuilder associated with this competition.
     *
     * @return groupBuilder associated with this competition
     */
    private GroupBuilder getGroupBuilder() {
        if (this.groupBuilder == null) {
            createGroupBuilder();
        }
        return groupBuilder;
    }

    /**
     * Allocates participants to competing and ranking groups.
     *
     * The competing and ranking groups are used to ensure the proper order
     * of the competition, and to determine the winners of the competition
     * after completion, respectively.
     */
    private void allocateGroups() {
        setRankingGroups(getGroupBuilder().createRankingGroups());
        setCompetingGroups(getGroupBuilder().createCompetingGroups());
    }

    /**
     * Finds the participant who is to carry out a lift next. If the competing
     * stage of the competition is complete, no value is returned.
     *
     * @return the participant next to left
     */
    public Optional<Participant> getCurrentParticipant() {
        Optional<Group> currentGroup = getCurrentCompetingGroup();
        if (currentGroup.isPresent()) {
            return Optional.of(currentGroup.get().getFirstParticipant());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Calculates a specific participant's rank within the competition.
     *
     * Rank is reported on a per-ranking group basis. As such, there may be
     * multiple participants with the same rank from different groups.
     * Additionally, multiple participants may have the same rank at a given
     * point in time, so there may be multiple participants with the same rank
     * in one group.
     *
     * @param participant                the participant for whom the ranking
     *                                   is being calculated
     * @return                           the passed participant's rank
     * @throws InvalidParameterException if participant cannot be found within
     *                                   any ranking group
     */
    public int getRank(Participant participant) throws InvalidParameterException {
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
        List<Integer> remainingNumbers = IntStream.rangeClosed(1, getMaxNumParticipants())
                .boxed()
                .collect(Collectors.toList());
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
        List<Participant> ps = getParticipants().stream()
                .filter(Participant::isNotWeighedIn)
                .collect(Collectors.toList());
        ps.forEach(this::removeParticipant);
        allocateGroups();
    }

    /**
     * Determines whether sign-up is open.
     *
     * Sign-up is open until after the {@link #lastRegistrationDate}.
     *
     * @return true, if sign-up open, else false
     */
    public boolean isSignUpOpen() {
        return getLastRegistrationDate().after(new Date());
    }

    /**
     * Determines whether sign-up is closed.
     *
     * Sign-up closes after the {@link #lastRegistrationDate} is reached.
     *
     * @return true, if sign-up closed, else false
     */
    public boolean isSignUpClosed() {
        return new Date().after(getLastRegistrationDate());
    }

    /**
     * Determines whether weigh-in has started but not yet completed.
     *
     * Weigh-in starts on the date of the competition, and ends when completion
     * is selected by the user (see {@link #isWeighInComplete()}).
     *
     * @return true, if weigh-in started, else false
     */
    public boolean isWeighInStarted() {
        return isCompetitionToday() && !isWeighInComplete();
    }

    /**
     * Determines whether the weigh-in is complete.
     *
     * A weigh-in is complete when determined by the user/competition secretary.
     * When complete, all groups are allocated. Prior to completion, there is no
     * allocation of groups.
     *
     * @return true, if weigh-in complete, else false
     *
     */
    public boolean isWeighInComplete() {
        return (!getCompetingGroups().isEmpty());
    }

    /**
     * Determines whether the competition has started.
     *
     * A competition has started when the weigh-in is complete, and lifting has
     * begun or is due to begin.
     *
     * @return true, if complete, else false
     */
    public boolean isCompetitionStarted() {
        return isWeighInComplete() && !isCompetitionComplete();
    }

    /**
     * Determines whether the competition is complete.
     *
     * A competition is complete when all participants have undertaken all of
     * the required lifts.
     *
     * @return true, if complete, else false
     */
    public boolean isCompetitionComplete() {
        if (isWeighInComplete()) {
            for (Participant p : getParticipants()) {
                if (!p.allLiftsComplete()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the competition is due to take place today.
     *
     * @return true, if competition is to take place today, else false
     */
    public boolean isCompetitionToday() {
        return areSameDay(new Date(), getCompetitionDate());
    }

    /**
     * Compares two Date objects to determine if they relate to the same day.
     *
     * @param d1 the first date to test
     * @param d2 the second date to test
     * @return true, if both dates relate to the same day, else false
     */
    private boolean areSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
