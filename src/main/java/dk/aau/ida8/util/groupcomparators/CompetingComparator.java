package dk.aau.ida8.util.groupcomparators;

import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Participant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a comparator to be used to compare participants within
 * a competing group in a competition.
 *
 * This class implements the Comparator interface, implementing the compare
 * method to provide a custom comparison between participants within a group.
 * See {@link #compare(Participant, Participant)} for details of this
 * comparison.
 */
public class CompetingComparator implements Comparator<Participant> {

    private Participant p1;
    private Participant p2;

    /**
     * Compares two participants based on a number of comparison
     * factors.
     *
     * The participants are compared on the following factors, in the following
     * order:-
     *
     * <ol>
     *     <li>Completion of all lifts;</li>
     *     <li>Next weight to be lifted;</li>
     *     <li>Number of completed attempts;</li>
     *     <li>Timestamp of the first snatch / clean & jerk lift; and</li>
     *     <li>The start numbers of the participants.</li>
     * </ol>
     *
     * As soon as one factor indicates a distinction between participants, this
     * is the relevant comparison and its value will be returned. For example,
     * if both lifters have completed two lifts, then the next weight of their
     * lifts will be considered, with lowest first. If they are the same, then
     * the number of completed attempts will be compared, again with lowest
     * going first. Then, if these are the same, timestamps of the first snatch
     * or clean & jerk lifts will be compared. If these are the same (this
     * should not be possible, but it is included for completeness) then start
     * numbers are compared.
     *
     * @param p1 The first participant to compare
     * @param p2 The second participant to compare
     * @return a negative value where p1 comes first, positive where
     *         p2 comes first, and zero where they are equal
     */
    @Override
    public int compare(Participant p1, Participant p2) {
        this.p1 = p1;
        this.p2 = p2;

        List<Integer> comparators = Arrays.asList(
                compareCompletions(),
                compareWeights(),
                compareAttempts(),
                compareTimestamps(),
                compareStartNumbers()
        );

        for (Integer comparatorValue : comparators) {
            if (comparatorValue != 0) {
                return comparatorValue;
            }
        }
        return 0;
    }

    /**
     * Compares two participants based on whether they have completed
     * all snatches, or all lifts.
     *
     * If one has not completed all snatches, this is ordered first.
     *
     * If both have completed all lifts, then return 0. If p1 has
     * completed all lifts, returns 1. If p2 has completed all lifts,
     * returns -1.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareCompletions() {
        int p1Count = p1.getLiftsCount();
        int p2Count = p2.getLiftsCount();

        // Check if both at 0 or 3 lifts
        if (p1Count == 0 && p2Count == 0 || p1Count == 3 && p2Count == 3)  {
            return 0;
        } else if (p1Count == 0) {
            return -1;
        } else if (p2Count == 0) {
            return 1;
        }
        // Compare for snatch completion first.
        if (p1Count < 3 ^ p2Count < 3) {
            if (p1Count < 3) {
                return -1;
            } else if (p2Count < 3) {
                return 1;
            }
        }
        // Then check for clean & jerk completion
        if (p1Count == 6 ^ p2Count == 6) {
            if (p1Count == 6) {
                return 1;
            } else {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Compares the lift weights of two participants.
     *
     * This method compares the weights next to be lifted by two
     * participants. The participant with the lower lift weight should
     * come first.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareWeights() {
        return p1.getCurrentWeight() - p2.getCurrentWeight();
    }

    /**
     * Compares the number of attempts of two participants.
     *
     * The participant with the fewer attempts should come first.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareAttempts() {
        return p1.getLiftsCount() - p2.getLiftsCount();
    }

    /**
     * Compares two Participants based on the timestamps of their first
     * completed lifts for snatch or C&J.
     *
     * If both have completed no lifts, then neither comes first.
     *
     * If one of the participants has fewer than three completed lifts, that is
     * he is doing snatch lifts, then the timestamps for the first completed snatch
     * lifts will be compared.
     *
     * If one of the participants has more than three completed lifts, that is he is
     * doing C&J lifts, then the timestamps for the first completed C&J lifts
     * will be compared.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareTimestamps() {
        int p1Count = p1.getLiftsCount();
        int p2Count = p2.getLiftsCount();

        if (p1Count > 0 && p1Count < 3 && p2Count > 0 && p2Count < 3) {
            Lift p1FirstSnatch = p1.getLifts().get(0);
            Lift p2FirstSnatch = p2.getLifts().get(0);
            return p1FirstSnatch.getTimestamp().compareTo(p2FirstSnatch.getTimestamp());
        } else if (p1Count > 3 && p1Count < 6 && p2Count > 3 && p2Count < 6){
            Lift p1FirstCJ = p1.getLifts().get(3);
            Lift p2FirstCJ = p2.getLifts().get(3);
            return p1FirstCJ.getTimestamp().compareTo(p2FirstCJ.getTimestamp());
        }
        return 0;
    }

    /**
     * Compares the ID#s of two participants.
     *
     * The participant with the lower ID should come first.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareStartNumbers() {
        return p1.getStartNumber() - p2.getStartNumber();
    }
}
