package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Lift implements Serializable {

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
