package dk.aau.ida8.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mikkelmoerch on 13/04/16.
 */
public class CompetitionTotalWeight extends Competition {

    private int[] weightClassesMen = {56, 62, 69, 77, 85, 94, 105};
    private HashMap<Integer, ArrayList<Participant>> groupsMen;
    private HashMap<Integer, ArrayList<Participant>> groupsWomen;

    @Override
    public HashMap<Integer, ArrayList<Participant>> allocateGroups() {

    }

    /**
     * This methods return the total score of a participant, which is the
     * best snatch added to the best clean and jerk.
     *
     * @param participant the participant for which to calculate the score
     * @return The total score of a participant
     */
    @Override
    public double calculateScore(Participant participant) {
        return participant.getBestCleanAndJerk() +
                participant.getBestSnatch();
    }

    @Override
    public List<Participant> calculateRankings() {
        return null;
    }
}
