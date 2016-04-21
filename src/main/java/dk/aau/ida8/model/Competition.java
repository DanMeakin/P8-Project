package dk.aau.ida8.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
public abstract class Competition {

    public enum CompetitionType {
        SINCLAIR,
        TOTAL_WEIGHT
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Participant> participants;

    // group list variables. Marked as transient so as not to persist them in the database
    private transient List<Group> groupList;
    private transient Group currentGroup;

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
        participants.add(p);
    }

    /**
     * Removes a participant from the list of participants.
     *
     * @param lifter who is to be removed
     */
    public void removeParticipant(Lifter lifter){
        Participant p = selectParticipationByLifter(lifter);
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
    public Participant selectParticipationByLifter(Lifter lifter) {
        List<Participant> ps = getParticipants().stream()
                .filter(p -> p.getLifter().equals(lifter))
                .collect(Collectors.toList());
        return ps.get(0);
    }

    public abstract List<Group> allocateGroups();

    /**
     * Finds the participant who is to carry out a lift next.
     *
     * @return the participant next to left
     */
    public Participant currentParticipant() {
        return getCurrentGroup().determineParticipationOrder().get(0);
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Lifter> getLifters() {
        return getParticipants().stream()
                .map(Participant::getLifter)
                .collect(Collectors.toList());
    }


    /**
     * Calculates and returns the rankings for this competition.
     *
     * The ranking uses the score for each participation calculated using the
     * associated ScoreStrategy.
     *
     * @return participants for this competition, ranked in order
     */
    public abstract void calculateRankings();

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

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void updateCurrentGroup() {

        Group g = findCurrentGroup();

        if (!g.equals(null)){
            this.currentGroup = g;
        }

    }

    private Group findCurrentGroup() {
        for(Group g : getGroupList()){
            for (Participant p : g.getParticipantList()){
                if (p.getLiftsCount() < 6) {
                    return g;
                }
            }
        }
        return null;
    }

    /**
     * Calculates a specific participant's rank
     * @param participant the participant for whom the ranking is being calculated
     * @return the passed participant's rank
     */
    public int getRank(Participant participant){
        // TODO obsolete method. reimplement to instead provide a ranking based on yet-to-be-implemented ranking groups
        return 10000;
        //return calculateRankings().indexOf(participant) + 1;
    }

}
