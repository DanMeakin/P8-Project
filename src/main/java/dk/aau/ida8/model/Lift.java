package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.LocalDateTime;

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
    private LocalDateTime timestamp;

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

    /**
     * Empty constructor required for Hibernate.
     */
    public Lift() {

    }

    /**
     * Creates a new Lift object.
     *
     * This private constructor is not intended to be used to create Lift
     * instances. See {@link #passedLift(Participant, LiftType, int)},
     * {@link #failedLift(Participant, LiftType, int)} and
     * {@link #abstainedLift(Participant, LiftType, int)} for constructors to be
     * used.
     *
     * @param participant the participant undertaking the lift
     * @param liftType    the type of lift: snatch or clean & jerk
     * @param weight      the weight in kg of the lift
     * @param outcome     the outcome, whether successful, failed or abstained
     */
    private Lift(Participant participant, LiftType liftType, int weight, LiftOutcome outcome) {
        this.participant = participant;
        this.liftType = liftType;
        this.outcome = outcome;
        this.weight = weight;

        // also instantiate an instant when the lift was completed
        this.timestamp = LocalDateTime.now();
    }


    /**
     * Gets the type of this lift.
     *
     * @return the type of this lift
     */
    public LiftType getLiftType() {
        return liftType;
    }

    /**
     * Gets the participant for this lift.
     *
     * @return the participant for this lift
     */
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

    /**
     * Determines whether this is a clean & jerk lift.
     *
     * @return true if clean & jerk, else false
     */
    public boolean isCleanAndJerk() {
        return getLiftType().equals(LiftType.CLEAN_AND_JERK);
    }

    /**
     * Determines whether this is a snatch lift.
     *
     * @return true if snatch, else false
     */
    public boolean isSnatch() {
        return getLiftType().equals(LiftType.SNATCH);
    }

    public Long getId() {
        return id;
    }

    /**
     * Gets the weight of this lift, in kg.
     *
     * @return the weight of this lift in kg
     */
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

    /**
     * Gets the outcome for this lift: pass, fail or abstain.
     *
     * @return the outcome for this lift
     */
    public LiftOutcome getOutcome() {
        return outcome;
    }

    /**
     * Determines whether this is a passed lift.
     *
     * @return true, if this is a passed lift, else false
     */
    public boolean isPassed() {
        return (getOutcome().equals(LiftOutcome.PASS));
    }

    /**
     * Determines whether this is a failed lift.
     *
     * @return true, if a failed lift, else false
     */
    public boolean isFailed() {
        return (getOutcome().equals(LiftOutcome.FAIL));
    }

    /**
     * Determines whether this is an abstained lift.
     *
     * @return true, if an abstained lift, else false
     */
    public boolean isAbstained() {
        return (getOutcome().equals(LiftOutcome.ABSTAIN));
    }

    /**
     * Gets the timestamp for this lift.
     *
     * @return the timestamp for this lift
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
