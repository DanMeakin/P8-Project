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
 * {@link Participation Participation} instance.
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
    private List<Participation> participations;

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
        this.participations = new ArrayList<>();
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
     * {@link Participation Participation} object. This method accepts a lifter
     * instance and then creates and aggregates the required Participation
     * object to the competition.
     *
     * @param lifter the lifter to add to the competition
     */
    public void addParticipant(Lifter lifter, int startingWeight){
        Participation p = new Participation(lifter, this, startingWeight);
        participations.add(p);
    }

    /**
     * Removes a participant from the list of participants.
     *
     * @param lifter who is to be removed
     */
    public void removeParticipant(Lifter lifter){
        Participation p = selectParticipationByLifter(lifter);
        removeParticipant(p);
    }

    /**
     * Removes a participant from the participations list.
     *
     * @param participation the participation object to remove
     */
    private void removeParticipant(Participation participation) {
        participations.remove(participation);
    }

    /**
     * Gets the participation object for the given lifter.
     *
     * @param lifter the lifter for which to obtain the participation
     *               instance
     * @return participation instance for the passed lifter
     */
    public Participation selectParticipationByLifter(Lifter lifter) {
        List<Participation> ps = getParticipations().stream()
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
    public Participation determineNextParticipation() {
        return determineParticipationOrder().get(0);
    }

    /**
     * Sorts and returns the list of participations.
     *
     * The identity of the next participant is calculated based on which
     * participant has chosen the lowest weight to lift. If two or more lifters
     * are to be lifting the same weight, the next participant is determined
     * by taking the lowest of these lifters' ID#.
     *
     * @return sorted list of participants
     */
    public List<Participation> determineParticipationOrder() {
        Collections.sort(getParticipations(), new Comparator<Participation>() {
            @Override
            public int compare(Participation p1, Participation p2) {
                int weightComp = p1.getCurrentWeight() - p2.getCurrentWeight();
                long idComp = p1.getLifter().getId() - p2.getLifter().getId();
                if (weightComp == 0) {
                    return (int) idComp;
                } else {
                    return weightComp;
                }
            }
        });
        return getParticipations();
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public List<Lifter> getLifters() {
        return getParticipations().stream()
                .map(Participation::getLifter)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the score for a given participation.
     *
     * This methods refers to the encapsulated score strategy within a
     * particular competition, and uses this to calculate the score for a
     * participation.
     *
     * @param participation the participation for which to calculate the score
     * @return the score for that participation
     */
    public double calculateScore(Participation participation) {
        return createScoreStrategy().calculateScore(participation);
    }

    /**
     * Calculates and returns the rankings for this competition.
     *
     * The ranking uses the score for each participation calculated using the
     * associated ScoreStrategy.
     *
     * @return participations for this competition, ranked in order
     */
    public List<Participation> calculateRankings() {
        return getParticipations().stream()
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
