package dk.aau.ida8.model.groupComparators;

import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Participant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CompetingComparator implements Comparator<Participant> {

    private Participant p1;
    private Participant p2;

    /**
     * Compares two participants based on a number of comparison
     * factors.
     *
     * The factors are listed in order within the "comparators" list,
     * and this list is iterated through to find the first non-zero
     * value. This is then returned by this method.
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
                compareIds()
        );

        for (Integer comparatorValue : comparators) {
            if (comparatorValue != 0) {
                return comparatorValue;
            }
        }
        return 0;
    }

    /**
     * Compares two Participants based on the timestamps of their last
     * completed lifts.
     *
     * If either has completed no lifts, then that participant comes
     * first. If both have completed no lifts, then neither comes
     * first.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareTimestamps() {
        int p1Count = p1.getLiftsCount();
        int p2Count = p2.getLiftsCount();
        if (p1Count == 0 && p2Count == 0) {
            return 0;
        } else if (p1Count == 0) {
            return -1;
        } else if (p2Count == 0) {
            return 1;
        }

        Lift p1Last = p1.getLifts().get(p1Count - 1);
        Lift p2Last = p2.getLifts().get(p2Count - 1);

        return p1Last.getTimestamp().compareTo(p2Last.getTimestamp());
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
        // Compare for snatch completion first.
        if (p1.getLiftsCount() < 3 ^ p2.getLiftsCount() < 3) {
            if (p1.getLiftsCount() < 3) {
                return -1;
            } else if (p2.getLiftsCount() < 3) {
                return 1;
            }
        }
        if (p1.liftsComplete() ^ p2.liftsComplete()) {
            if (p1.liftsComplete()) {
                return 1;
            } else if (p2.liftsComplete()) {
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
     * Compares the ID#s of two participants.
     *
     * The participant with the lower ID should come first.
     *
     * @return comparator value after carrying-out comparison
     */
    private int compareIds() {
        return (int) (p1.getId() - p2.getId());
    }

}
