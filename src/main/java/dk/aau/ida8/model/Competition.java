package dk.aau.ida8.model;

import java.util.*;
import java.util.stream.Collectors;

public class Competition {

    @Id
    @GeneratedValue
    private long id;
    private CompetitionType type;
    private List<Participation> participations;
    private String competitionName;
    private Address location;
    private Club host;
    private Date date;

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
     * @param type            the type of competition, e.g. Sinclair, weight
     *                        classes
     * @param date            the date on which the competition is to take place
     */
    public Competition(String competitionName, Club host, Address location, CompetitionType type, Date date) {
        this.competitionName = competitionName;
        this.type = type;
        this.location = location;
        this.date = date;
        this.host = host;
        this.participations = new ArrayList<>();
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
     * The identity of the next participant is calculated based on which
     * participant has chosen the lowest weight to lift. If two or more lifters
     * are to be lifting the same weight, the next participant is determined
     * by taking the lowest of these lifters' ID#.
     *
     * @return the participation object containing the next lifter who is to
     *         lift
     */
    public Participation determineNextParticipation() {
        return determineParticipationOrder().get(0);
    }

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

    public CompetitionType getType() {
        return type;
    }

    public void setType(CompetitionType type) {
        this.type = type;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public List<Lifter> getLifters() {
        return getParticipations().stream()
                .map(Participation::getLifter)
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

    public Club getHost() {
        return host;
    }

    public void setHost(Club host) {
        this.host = host;
    }
}
