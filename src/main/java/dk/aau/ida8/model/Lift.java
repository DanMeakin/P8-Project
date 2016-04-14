package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.security.InvalidParameterException;

/**
 * This class represents one lift carried out within a weightlifting
 * competition.
 *
 * A lift may be one of several types: a snatch or a clean and jerk. The weight
 * of a lift and a flag indicating whether it was successful are stored with
 * the lift.
 *
 * The class provides methods for determining the score for a lift (this will
 * be the weight lifted if successful, or 0 if not).
 */
@Entity
public class Lift {

    /**
     * Defines the types of lift which may be carried out by a Lifter.
     */
    public enum LiftType {
        SNATCH, CLEAN_AND_JERK
    }

    /**
     * Defines the outcome for a given lift.
     *
     * There are three possible outcomes for a lift: Pass, Fail or Abstain. A
     * lift passes where the lifter successfully completes a lift; it fails
     * where the lifter is unable to complete; and it is abstained where the
     * lifter declines to attempt that lift.
     */
    public enum LiftOutcome {
        PASS, FAIL, ABSTAIN
    }

    @Id
    @GeneratedValue
    private Long id;

    private LiftOutcome outcome;
    private LiftType liftType;
    private int weight;

    @ManyToOne
    private Participant participant;

    /**
     * Creates a new Lift object representing a successful/passed lift.
     *
     * @param participant the participant undertaking the lift
     * @param liftType type of lift - snatch or clean & jerk
     * @param weight the weight lifted
     * @return a Lift object containing values as passed to this method
     */
    public static Lift passedLift(Participant participant, LiftType liftType, int weight) {
        return new Lift(participant, liftType, weight, LiftOutcome.PASS);
    }

    /**
     * Creates a new Lift object representing an unsuccessful/failed lift.
     *
     * @param participant the participant undertaking the lift
     * @param liftType type of lift - snatch or clean & jerk
     * @param weight the weight lifted
     * @return a Lift object containing values as passed to this method
     */
    public static Lift failedLift(Participant participant, LiftType liftType, int weight) {
        return new Lift(participant, liftType, weight, LiftOutcome.FAIL);
    }

    /**
     * Creates a new Lift object representing an abstained lift.
     *
     * @param participant the participant undertaking the lift
     * @param liftType type of lift - snatch or clean & jerk
     * @param weight the weight lifted
     * @return a Lift object containing values as passed to this method
     */
    public static Lift abstainedLift(Participant participant, LiftType liftType, int weight) {
        return new Lift(participant, liftType, weight, LiftOutcome.ABSTAIN);
    }

    public Lift() {

    }

    private Lift(Participant participant, LiftType liftType, int weight, LiftOutcome outcome) {
        this.participant = participant;
        this.liftType = liftType;
        this.outcome = outcome;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Lift: " + getLiftType() + " - " + getWeight() + " (" + getOutcome() + ")";
    }

    public LiftType getLiftType() {
        return liftType;
    }

    public Participant getParticipant() {
        return participant;
    }

    /**
     * Calculate the raw score for this lift.
     *
     * The score for a successful lift is equal to the weight lifted. An
     * unsuccessful lift scores zero.
     *
     * @return lift score
     */
    public int getScore() {
        if (isPassed()) {
            return getWeight();
        } else {
            return 0;
        }
    }

    public boolean isCleanAndJerk() {
        return getLiftType().equals(LiftType.CLEAN_AND_JERK);
    }

    public boolean isSnatch() {
        return getLiftType().equals(LiftType.SNATCH);
    }

    public Long getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight for a particular lift.
     *
     * @param weight the weight to set the lift to
     * @throws InvalidParameterException if weight passed is less than or
     *                                   equal to zero
     */
    public void setWeight(int weight) throws InvalidParameterException {
        if (weight <= 0) {
            String msg = "unable to set weight to less than 1kg";
            throw new InvalidParameterException(msg);
        }
        this.weight = weight;
    }

    public LiftOutcome getOutcome() {
        return outcome;
    }

    public boolean isPassed() {
        return (getOutcome().equals(LiftOutcome.PASS));
    }

    public boolean isFailed() {
        return (getOutcome().equals(LiftOutcome.FAIL));
    }

    public boolean isAbstained() {
        return (getOutcome().equals(LiftOutcome.ABSTAIN));
    }
}
