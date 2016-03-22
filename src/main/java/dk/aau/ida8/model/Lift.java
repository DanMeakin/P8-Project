package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

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
        SNATCH, CLEANANDJERK
    }

    @Id
    @GeneratedValue
    private Long id;

    private boolean isAccepted;
    private LiftType liftType;
    private int weight;

    public Lift(LiftType liftType, int weight) {
        this.liftType = liftType;
        this.isAccepted = false;
        this.weight = weight;
    }

    public LiftType getLiftType() {
        return liftType;
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
        if (isAccepted()) {
            return getWeight();
        } else {
            return 0;
        }
    }

    public boolean isCleanAndJerk() {
        return getLiftType().equals(LiftType.CLEANANDJERK);
    }

    public boolean isSnatch() {
        return getLiftType().equals(LiftType.SNATCH);
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
