package dk.aau.ida8.model;

import org.omg.CORBA.DynAnyPackage.Invalid;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the participation of one lifter in a competition. It
 * contains methods and attributes related to the performance of a lifter within
 * a competition.
 *
 * A lifter undertakes six different lifts within a competition: three of these
 * are snatch, and three are clean & jerk. Lifts are aggregated to a
 * participation instance. Logic is found within the participation class to
 * handle these constraints.
 *
 * It should not be possible to carry-out more than six lifts, and it should not
 * be possible to carry-out more than three lifts of each type.
 */
@Entity
public class Participant {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Lifter lifter;

    @ManyToOne
    private Competition competition;
    private int currentWeight;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Lift> lifts;


    public long getId() {
        return id;
    }

    /**
     * Default constructor required for Hibernate.
     */
    public Participant() {

    }

    /**
     * Creates a participation instance.
     *
     * @param lifter         the lifter participating in a competition
     * @param competition    the competition in which the lifter is
     *                       participating
     * @param startingWeight the initial weight to be lifted by the lifter
     */
    public Participant(Lifter lifter, Competition competition, int startingWeight) {
        this.lifter = lifter;
        this.competition = competition;
        this.currentWeight = startingWeight;
        this.lifts = new ArrayList<>();
    }

    public Lifter getLifter() {
        return lifter;
    }

    public Competition getCompetition() {
        return competition;
    }

    /**
     * Gets the list of all lifts undertaken during this participation.
     *
     * @return list of all lifts undertaken
     */
    public List<Lift> getLifts() {
        return lifts;
    }

    /**
     * Gets the current chosen weight to lift.
     *
     * @return current weight
     */
    public int getCurrentWeight() {
        return currentWeight;
    }

    public String getFullName() {
        return getLifter().getFullName();
    }

    /**
     * Sets the weight this participant chooses to lift next.
     *
     * Weight can only increase. As such, this method ensures that the new
     * weight is not less than the current weight. If the new weight is lower
     * than existing, an IllegalArgumentException is thrown.
     *
     * @param newWeight the weight this lifter will be lifting next
     */
    public void setCurrentWeight(int newWeight) throws IllegalArgumentException {
        if (newWeight < getCurrentWeight()) {
            String msg = "unable to set new weight to " + newWeight + "kg; " +
                         "current weight of " + this.currentWeight + " kg " +
                         "is greater than new weight";
            throw new IllegalArgumentException(msg);
        }
        this.currentWeight = newWeight;
    }

    /**
     * Calculates the total score for this participation.
     *
     * This method uses the ScoreStrategy associated with the Competition
     * in which this participation takes place. The strategy calculates the
     * proper score, and this method returns this score.
     *
     * @return the total score for this participation
     */
    public double getTotalScore() {
        return getCompetition().calculateScore(this);
    }

    /**
     * Gets the weight of the best successful clean & jerk lift from this
     * participation.
     *
     * @return the best clean & jerk lift weight
     */
    public int getBestCleanAndJerk() {
        return getLifts().stream()
                .filter(Lift::isCleanAndJerk)
                .max(scoreComparator())
                .get()
                .getScore();
    }

    /**
     * Gets the weight of the best successful snatch lift from this
     * participation.
     *
     * @return the best snatch lift weight
     */
    public int getBestSnatch() {
         return getLifts().stream()
                 .filter(Lift::isSnatch)
                 .max(scoreComparator())
                 .get()
                 .getScore();
    }

    /**
     * Defines a comparator which is used to sort/select lifts based on their
     * score.
     *
     * @return the lift comparator
     */
    private Comparator<Lift> scoreComparator() {
        return (l1, l2) -> l1.getScore() - l2.getScore();
    }

    /**
     * Gets the type of lift this participant is due to undertake next.
     *
     * This method checks the list of completed Lifts associated with a given
     * participant.
     *
     * If this list has fewer than three completed Snatch lifts
     * contained in it, then the participant will be carrying out a Snatch
     * next.
     *
     * If this list has three completed Snatch lifts but fewer than three
     * completed Clean & Jerk lifts, then the participant will be carrying out
     * a Clean & Jerk next.
     *
     * If this list has three of each type of activity, then there is no further
     * lift to be completed, and this method will return null.
     *
     * @return the type of lift to carry-out next, or null if no further lifts
     *         to complete
     */
    public Lift.LiftType getCurrentLiftType() {
        if (getLifts().stream()
                .filter(l -> l.isSnatch())
                .collect(Collectors.toList())
                .size() < 3) {
            return Lift.LiftType.SNATCH;
        } else if (getLifts().stream()
                .filter(l -> l.isCleanAndJerk())
                .collect(Collectors.toList())
                .size() < 3) {
            return Lift.LiftType.CLEAN_AND_JERK;
        } else {
            return null;
        }
    }

     /**
     * Creates and adds a passed lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addPassedLift() throws InvalidParameterException {
        Lift lift = Lift.passedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
    }

    /**
     * Creates and adds a failed lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addFailedLift() throws InvalidParameterException {
        Lift lift = Lift.failedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
    }

    /**
     * Creates and adds an abstained lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addAbstainedLift() throws InvalidParameterException {
        Lift lift = Lift.abstainedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
    }

    /**
     * Adds a lift to a participation instance.
     *
     * @param lift the lift to add to the participation
     */
    private void addLift(Lift lift) throws InvalidParameterException {
        validateLiftConditions(lift);
        lifts.add(lift);
    }

    /**
     * Validates that a lift which is to be added to this participation is valid.
     *
     * A lift is considered to be valid where fewer than six lifts in total have
     * been carried out as part of this participation, and where fewer than
     * three lifts of the same type as that of the lift passed have been
     * undertaken.
     *
     * This method throws an exception if the lift conditions are invalid. If
     * they are valid, no exception is thrown.
     *
     * @param lift the lift which is to be checked for validity
     */
    private void validateLiftConditions(Lift lift) throws InvalidParameterException {
        if (lift.getOutcome() == null) {
            String msg = "unable to create lift with null outcome; " +
                    "participant has already completed six lifts";
            throw new InvalidParameterException(msg);
        }
    }
}
