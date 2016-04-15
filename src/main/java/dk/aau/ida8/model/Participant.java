package dk.aau.ida8.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.omg.CORBA.DynAnyPackage.Invalid;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.*;
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

    /**
     * This integer is used to track the number of weight changes made by a
     * participant between lifts.
     */
    private int weightChanges = 0;

    @ManyToOne
    private Lifter lifter;

    @ManyToOne
    private Competition competition;

    /**
     * used to calculate the starting groups for a sinclair competition
     */
    private int startingWeight;

    private int currentWeight;

    /**
     * This integer tracks the weight previously selected by the lifter. This
     * is used where it is necessary to revert to the previously selected
     * weight where, for example, a user has changed the weight of a
     * participant in error.
     */
    private int previousWeight;

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
        this.startingWeight = startingWeight;
        this.currentWeight = startingWeight;
        this.previousWeight = startingWeight;
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
     * Gets a count of the number of completed lifts by the participant.
     *
     * @return the number of lifts completed by this participant in this
     *         competition
     */
    public int getLiftsCount() {
        return getLifts().size();
    }

    /**
     * Gets a count of the number of lifts yet to be complete by the
     * participant.
     *
     * @return the number of lifts yet to be completed by the participant in
     *         this competition
     */
    public int getLiftsRemaining() {
        return 6 - getLiftsCount();
    }

    /**
     * Determines whether this participant has completed their lifts for this
     * competition.
     *
     * @return true if lifts are all complete, else false
     */
    public boolean liftsComplete() {
        return getLiftsCount() == 6;
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
     * Gets the weight the lifter had elected to lift immediately before the
     * current weight.
     *
     * @return the previous weight
     */
    public int getPreviousWeight() {
        return previousWeight;
    }

    public String getFullName() {
        return getLifter().getFullName();
    }

    /**
     * Sets the weight this participant chooses to lift next.
     *
     * This method is used only to set the weight. A weight change may occur in
     * certain circumstances: a participant may have successfully completed a
     * lift, in which case the weight is increased automatically by 1kg (see
     * {@link #incrementWeight() incrementWeight}); or a participant may elect
     * to increase the next weight they will lift (see
     * {@link #increaseWeight(int) increaseWeight}) Additionally, an erroneous
     * increase can be corrected by the user
     * (see {@link #correctWeight(int) correctWeight} and
     * {@link #revertWeight() revertWeight}).
     *
     * @param newWeight the weight this participant will be lifting next
     */
    private void setCurrentWeight(int newWeight) {
        this.previousWeight = getCurrentWeight();
        this.currentWeight = newWeight;
    }

    /**
     * Gets the number of weight changes made by a participant since the
     * immediately previous lift (or before the first lift).
     *
     * @return the number of weight changes made
     */
    public int getWeightChanges() {
        return weightChanges;
    }

    /**
     * Sets the number of weight changes made by a participant since the
     * immediately previous lift (or before the first lift).
     *
     *
     * @param weightChanges the number of weight changes made
     */
    private void setWeightChanges(int weightChanges) {
        this.weightChanges = weightChanges;
    }

    /**
     * Increments the weight to be lifted by the participant.
     *
     * This method is called after the completion of a successful lift, where
     * the rules require that the next weight to be lifted must be at least
     * one kilogram higher than that just lifted.
     *
     */
    private void incrementWeight() {
        setCurrentWeight(getCurrentWeight() + 1);
    }


    /**
     * A participant may only increase the weight to be lifted (not decrease
     * it). As such, this method checks that the new weight is greater than the
     * existing weight.
     *
     * @param newWeight
     * @return true if a weight change is valid
     */
    public boolean canIncreaseWeight(int newWeight){
        return newWeight > getCurrentWeight();
    }

    /**
     * A participant may only change weights twice between lifts
     * (or before the first lift). This method ensures that a change cannot be
     * made if the participant has already made two changes in weight.
     *
     * @return true if the participant can increase weight
     */
    public boolean canChangeWeight(){
        return getWeightChanges() < 2;
    }

    /**
     * Increases the weight to be lifted by the participant.
     *
     * @param newWeight the weight the participant has chosen to lift next
     * @throws InvalidParameterException if the specified new weight is not
     *                                   greater than the current weight
     * @throws UnsupportedOperationException if the lifter has already changed
     * weights twice since the previous lift
     */
    public void increaseWeight(int newWeight) throws InvalidParameterException, UnsupportedOperationException {
        if (!canIncreaseWeight(newWeight)) {
            String msg = "new weight must be greater than existing weight; " +
                    "current weight of " + getCurrentWeight() + " is greater " +
                    "than or equal to new weight of " + newWeight;
            throw new InvalidParameterException(msg);
        } else if (!canChangeWeight()) {
            String msg = "unable to increase weight: two changes have " +
                    "already been made";
            throw new UnsupportedOperationException(msg);
        }
        setCurrentWeight(newWeight);
        setWeightChanges(getWeightChanges() + 1);
    }

    /**
     * Corrects the current weight to be lifted to a new value.
     *
     * This method is to be used where a participant's select lift weight has
     * been increased in error.
     *
     * @param newWeight the corrected weight which the participant will lift
     *                  next
     */
    public void correctWeight(int newWeight) {
        setCurrentWeight(newWeight);
    }

    /**
     * Reverts the current weight to be lifted to its previous value.
     *
     * This method is intended for use where a participant's selected lift
     * weight has been changed in error, and must return to its previous value.
     * (It is intended as a sort of "undo" for a weight change, where the user
     * has, for example, changed the weight of the wrong participant.)
     */
    public void revertWeight() {
        setCurrentWeight(getPreviousWeight());
        setWeightChanges(getWeightChanges() - 1);
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
     * Gets the combined total value of the best snatch and best clean & jerk
     * weights.
     *
     * @return the combined total of the best snatch and best clean & jerk
     */
    public int getBestTotal() {
        return getBestSnatch() + getBestCleanAndJerk();

    }

    /**
     * Gets the weight of the best successful clean & jerk lift from this
     * participation.
     *
     * @return the best clean & jerk lift weight
     */
    public int getBestCleanAndJerk() {
        Optional<Lift> l = getLifts().stream()
                .filter(Lift::isCleanAndJerk)
                .max(scoreComparator());

        if (l.isPresent()) {
            return l.get().getScore();
        } else {
            return 0;
        }
    }

    /**
     * Gets the weight of the best successful snatch lift from this
     * participation.
     *
     * @return the best snatch lift weight
     */
    public int getBestSnatch() {
        Optional<Lift> l = getLifts().stream()
                 .filter(Lift::isSnatch)
                 .max(scoreComparator());

        if (l.isPresent()) {
            return l.get().getScore();
        } else {
            return 0;
        }
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
        if (snatchCount() < 3) {
            return Lift.LiftType.SNATCH;
        } else if (cleanAndJerkCount() < 3) {
            return Lift.LiftType.CLEAN_AND_JERK;
        } else {
            return null;
        }
    }

    /**
     * Counts the number of snatch lifts completed.
     *
     * @return the number of snatch lifts completed by this participant
     */
    public int snatchCount() {
        return (int) (getLifts().stream()
                .filter(l -> l.isSnatch())
                .count());
    }

    /**
     * Counts the number of clean & jerk lifts completed.
     *
     * @return the number of clean & jerk lifts completed by this participant
     */
    public int cleanAndJerkCount() {
        return (int) (getLifts().stream()
                .filter(l -> l.isCleanAndJerk())
                .count());
    }

     /**
     * Creates and adds a passed lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addPassedLift() throws InvalidParameterException {
        validateLiftConditions();
        Lift lift = Lift.passedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
        incrementWeight();
        setWeightChanges(0);
    }

    /**
     * Creates and adds a failed lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addFailedLift() throws InvalidParameterException {
        validateLiftConditions();
        Lift lift = Lift.failedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
        setWeightChanges(0);
    }

    /**
     * Creates and adds an abstained lift to a participation instance.
     *
     * An InvalidParameterException will be thrown if the lift is of a type
     * which has been fully completed.
     *
     */
    public void addAbstainedLift() throws InvalidParameterException {
        validateLiftConditions();
        Lift lift = Lift.abstainedLift(this, getCurrentLiftType(), getCurrentWeight());
        addLift(lift);
        setWeightChanges(0);
    }

    /**
     * Adds a lift to a participation instance.
     *
     * @param lift the lift to add to the participation
     */
    private void addLift(Lift lift) {
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
     */
    private void validateLiftConditions() throws InvalidParameterException {
        if (getCurrentLiftType() == null) {
            String msg = "participant has already completed six lifts";
            throw new InvalidParameterException(msg);
        }
    }

    /****************************
     * LIFTER ATTRIBUTE GETTERS *
     ****************************/


    public Lifter.Gender getGender() {
        return getLifter().getGender();
    }

    public String getGenderInitial() {
        return getGender().toString().substring(0, 1);
    }

    public Date getDateOfBirth() {
        return getLifter().getDateOfBirth();
    }

    public String getClubName() {
        return getLifter().getClubName();
    }

    public double getBodyWeight() {
        return getLifter().getBodyWeight();
    }

    // added getter and setter for the new startingWeight value
    public int getStartingWeight() {
        return startingWeight;
    }

    public void setStartingWeight(int startingWeight) {
        this.startingWeight = startingWeight;
    }
}
