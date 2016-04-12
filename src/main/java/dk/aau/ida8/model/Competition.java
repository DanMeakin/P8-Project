package dk.aau.ida8.model;

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
public class Competition {

    public enum CompetitionType {
        SINCLAIR, TOTAL_WEIGHT
    }

    @Id
    @GeneratedValue
    private long id;

    @OneToMany
    private List<Participant> participants;

    private String competitionName;
    private CompetitionType competitionType;
    private LocalDate date;

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
    public Competition(String competitionName, Club host, Address location, CompetitionType competitionType, LocalDate date) {
        this.competitionName = competitionName;
        this.competitionType = competitionType;
        this.location = location;
        this.date = date;
        this.host = host;
        this.participants = new ArrayList<>();
    }

    public Competition() {
    }

    /**
     * Factory method for creating a ScoreStrategy object.
     *
     * @return
     */
    private ScoreStrategy createScoreStrategy() {
        ScoreStrategy strategy = null;
        switch (competitionType.toString()) {
            case "SINCLAIR":
                strategy = new SinclairStrategy();
                break;
            case "TOTAL_WEIGHT":
                strategy = new TotalStrategy();
                break;
        }
        return strategy;
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

    /**
     * Determines the next participant to carry out a lift.
     *
     * @return the participation object containing the next lifter who is to
     *         lift
     */
    public Participant determineNextParticipation() {
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
                int weightComp = p1.getCurrentWeight() - p2.getCurrentWeight();
                long idComp = p1.getLifter().getId() - p2.getLifter().getId();
                if (weightComp == 0) {
                    return (int) idComp;
                } else {
                    return weightComp;
                }
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
     * Calculates the score for a given participant.
     *
     * This methods refers to the encapsulated score strategy within a
     * particular competition, and uses this to calculate the score for a
     * participant.
     *
     * @param participant the participant for which to calculate the score
     * @return the score for that participant
     */
    public double calculateScore(Participant participant) {
        return createScoreStrategy().calculateScore(participant);
    }

    /**
     * Calculates and returns the rankings for this competition.
     *
     * The ranking uses the score for each participation calculated using the
     * associated ScoreStrategy.
     *
     * @return participants for this competition, ranked in order
     */
    public List<Participant> calculateRankings() {
        return getParticipants().stream()
                .sorted((p1, p2) -> (int) Math.round(p2.getTotalScore() - p1.getTotalScore()))
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Club getHost() {
        return host;
    }

    public void setHost(Club host) {
        this.host = host;
    }
}
