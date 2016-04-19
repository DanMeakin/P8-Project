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

    public abstract List<List<Participant>> allocateGroups();

    /**
     * Finds the participant who is to carry out a lift next.
     *
     * @return the participant next to left
     */
    public Participant currentParticipant() {
        return determineParticipationOrder().get(0);
    }

    /**
     * Sorts and returns the list of participants.
     *
     * The identity of the next participant is calculated based on which
     * participant has chosen the lowest weight to lift. If two or more lifters
     * are to be lifting the same weight, the next participant is determined
     * by taking the lowest of these lifters' ID#.
     *
     * @return sorted list of participants
     */
    public List<Participant> determineParticipationOrder() {
        Collections.sort(getParticipants(), new Comparator<Participant>() {
            @Override
            public int compare(Participant p1, Participant p2) {
                int completionComp = 0;
                int weightComp = p1.getCurrentWeight() - p2.getCurrentWeight();
                int attemptsComp = p1.getLiftsCount() - p2.getLiftsCount();

                int timestampComp = 0;
                if ((p1.getLiftsCount() > 0 && p1.getLiftsCount() < 3) && (p2.getLiftsCount() > 0 && p2.getLiftsCount() < 3)) {
                    if (p1.getLifts().get(0).getTimeLiftCompleted().isBefore(p2.getLifts().get(0).getTimeLiftCompleted())){
                        timestampComp = 1;
                    } else {
                        timestampComp = -1;
                    }
                } else {
                    if (p1.getLifts().get(3).getTimeLiftCompleted().isBefore(p2.getLifts().get(3).getTimeLiftCompleted())){
                        timestampComp = 1;
                    } else {
                        timestampComp = -1;
                    }
                }

                long idComp = p1.getLifter().getId() - p2.getLifter().getId();

                if (p1.liftsComplete() || p2.liftsComplete()) {
                    if (p1.liftsComplete()) {
                        completionComp = 1;
                    } else if (p2.liftsComplete()) {
                        completionComp = -1;
                    }
                }

                List<Integer> comparators = Arrays.asList(
                        completionComp,
                        weightComp,
                        attemptsComp,
                        timestampComp,
                        (int) idComp
                );
                for (Integer comparatorValue : comparators) {
                    if (comparatorValue != 0) {
                        return comparatorValue;
                    }
                }
                return 0;
            }
        });
        return getParticipants();
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
    public abstract List<Participant> calculateRankings();

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
     * Calculates a specific participant's rank
     * @param participant the participant for whom the ranking is being calculated
     * @return the passed participant's rank
     */
    public int getRank(Participant participant){
        return calculateRankings().indexOf(participant) + 1;
    }

}
