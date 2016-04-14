package dk.aau.ida8.model;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
public class CompetitionTotalWeight extends Competition {

    private int[] weightClassesMen = {56, 62, 69, 77, 85, 94, 105, 106};
    private int[] weightClassesWomen = {48, 53, 58, 63, 69, 75, 76};

    private HashMap<Integer, ArrayList<Participant>> groupsMen;
    private HashMap<Integer, ArrayList<Participant>> groupsWomen;

    public CompetitionTotalWeight() {
    }

    public CompetitionTotalWeight(String competitionName, Club host, Address location, CompetitionType competitionType, Date date){
        super(competitionName, host, location, competitionType, date);
        this.groupsMen = new HashMap<Integer, ArrayList<Participant>>();
        this.groupsWomen = new HashMap<Integer, ArrayList<Participant>>();
    }

    @Override
    public void allocateGroups() {
        divideParticipantsByGenderAndWeight();

    }

    private void divideParticipantsByGenderAndWeight(){
        for(Participant p : getParticipants()){
            if(p.getLifter().getGender().equals(Lifter.Gender.MALE)){
                addParticipantToGroup(p);
            } else {
                addParticipantToGroup(p);
            }
        }
    }

    private int checkBodyWeight(Participant p){
        int bodyWeight = (int) p.getLifter().getBodyWeight();

        for(int i = 0; i < weightClassesMen.length; i++){
            if(bodyWeight == weightClassesMen[i]){
                return bodyWeight;
            }

            if(p.getLifter().getGender().equals(Lifter.Gender.MALE)) {
                return weightClassesMen[weightClassesMen.length - 1];
            } else {
                return weightClassesWomen[weightClassesWomen.length - 1];
            }
        }

        // Don't know the best practice here. Would it be better to return null?
        return 0;
    }

    /**
     * Predicate method
     * @param p
     * @return True if the key exists in the HashMap, False otherwise
     */
    private boolean checkKeyExists(Participant p) {
        if(this.groupsMen.containsKey(checkBodyWeight(p))){
            return true;
        }
        return false;
    }

    /**
     * Adds a participant to a weight group represented in the HashMap.
     * If the weight group exists, add partcipant. If not, add new weight group.
     * @param p
     */
    private void addParticipantToGroup(Participant p) {
        if(checkKeyExists(p)){
            this.groupsMen.get(checkBodyWeight(p)).add(p);
        } else {
            this.groupsMen.put(checkBodyWeight(p), new ArrayList<Participant>());
            this.groupsMen.get(checkBodyWeight(p)).add(p);
        }
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
