package dk.aau.ida8.util.groupcomparators;

import dk.aau.ida8.model.Participant;

import java.util.Comparator;

/**
 * This class represents a comparator to be used to compare participants within
 * a ranking group in a total weight competition.
 *
 * This class implements the Comparator interface, implementing the compare
 * method to provide a custom comparison between participants within a group.
 * The comparator simply compares total scores, with the participant with
 * the higher score coming before the participant with the lower score.
 */
public class TotalWeightRankingComparator implements Comparator<Participant> {

   /**
     * Compares two participants based on their total score.
     *
     * This method compares two participants based on each participant's
     * total score. The participant with the higher score comes before the
     * participant with the lower score. This results in a list sorted using
     * this comparator being sorted by score, with the highest coming first.
     *
     * @param p1 the first participant to compare
     * @param p2 the second participant to compare
     * @return a negative value where p1 comes first, positive where
     *         p2 comes first, and zero where they are equal
     */
    @Override
    public int compare(Participant p1, Participant p2) {
        return p2.getTotalScore() - p1.getTotalScore();
    }
}
