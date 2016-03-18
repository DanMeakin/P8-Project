package dk.aau.ida8.model;

import org.omg.CORBA.DynAnyPackage.Invalid;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
public class Participation {

    private Lifter lifter;
    private Competition competition;
    private int currentWeight;
    private List<Lift> lifts;

    /**
     * Gets the list of all lifts undertaken during this participation.
     *
     * @return list of all lifts undertaken
     */
    public List<Lift> getLifts() {
        return lifts;
    }

    /**
     * Creates a participation instance.
     *
     * @param lifter         the lifter participating in a competition
     * @param competition    the competition in which the lifter is
     *                       participating
     * @param startingWeight the initial weight to be lifted by the lifter
     */
    public Participation(Lifter lifter, Competition competition, int startingWeight) {
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
     * Gets the current chosen weight to lift.
     *
     * @return current weight
     */
    public int getCurrentWeight() {
        return currentWeight;
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
        if (this.currentWeight >= newWeight) {
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
        return getCompetition().getScoreStrategy().calculateScore(this);
    }

    /**
     * Gets the best clean & jerk lift from this participation.
     *
     * @return the best clean & jerk lift
     */
    public Lift getBestCleanAndJerk() {
        return getLifts().stream()
                .filter(Lift::isCleanAndJerk)
                .max(scoreComparator())
                .get();
    }

    /**
     * Gets the best snatch lift from this participation.
     *
     * @return the best snatch lift
     */
    public Lift getBestSnatch() {
         return getLifts().stream()
                .filter(Lift::isSnatch)
                .max(scoreComparator())
                .get();
    }

    /**
     * Defines a comparator which is used to sort/select lifts based on their
     * score.
     *
     * @return the lift comparator
     */
    private Comparator<Lift> scoreComparator() {
        return new Comparator<Lift>() {
            @Override
            public int compare(Lift l1, Lift l2) {
                return l1.getScore() - l2.getScore();
            }
        };
    }


    /**
     * Creates and adds a lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     * @param activity   the type of lift undertaken
     * @param successful flag whether this particular lift was successful or
     *                   not
     */
    public void addLift(Lift.LiftType activity, boolean successful) throws InvalidParameterException {
        Lift lift = new Lift(activity, getCurrentWeight());
        lift.setAccepted(successful);
        addLift(lift);
    }

    /**
     * Adds a lift to a participation instance.
     *
     * @param lift
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
        if (getLifts().size() >= 6) {
            String msg = "unable to add lift: six lifts already completed";
            throw new InvalidParameterException(msg);
        } else {
            // We need extra logic in here to compare lift activity with the
            // activities already listed. The lift class does not contain any
            // details of the activity undertaken during a lift, so this needs
            // to be added before we can complete this.
            return;
        }
    }
}
