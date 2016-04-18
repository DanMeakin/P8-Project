package dk.aau.ida8.model;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class CompetitionTotalWeight extends Competition {

    private double[] weightClassesMen = {56, 62, 69, 77, 85, 94, 105};
    private double[] weightClassesWomen = {48, 53, 58, 63, 69, 75};

    private HashMap<Integer, ArrayList<Participant>> groupsMen;
    private HashMap<Integer, ArrayList<Participant>> groupsWomen;

    public CompetitionTotalWeight() {

    }

    public CompetitionTotalWeight(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants){
        super(competitionName, host, location, competitionType, date, lastRegistrationDate, maxNumParticipants);
        this.groupsMen = new HashMap<Integer, ArrayList<Participant>>();
        this.groupsWomen = new HashMap<Integer, ArrayList<Participant>>();
    }

    /**
     * This!
     *
     */
    @Override
    public List<List<Participant>> allocateGroups() {
        List<List<Participant>> completeList = new ArrayList<>();

        divideParticipantsByGenderAndWeight(getParticipants());

        for(ArrayList<Participant> list : groupsWomen.values()){
            // Creating a list of subgroups that each has a max of 10 participants
            List<List<Participant>> newList = createSubgroups(list);
            // Each subgroup is added sequentially to the completeList
            for(List<Participant> lp : newList){
                completeList.add(lp);
            }
        }

        // Looping through the HashMap for lists of participants based on weight class and gender
        for(ArrayList<Participant> list : groupsMen.values()){
            // Creating a list of subgroups that each has a max of 10 participants
            List<List<Participant>> newList = createSubgroups(list);
            // Each subgroup is added sequentially to the completeList
            for(List<Participant> lp : newList){
                completeList.add(lp);
            }
        }

        //Collections.sort(completeList, (l1,l2) -> l1.get(0).getStartingWeight() - l2.get(0).getStartingWeight());

        /**
         * Printing to console for testing
         */
        System.out.println("--------");
        System.out.println("Test of allocateGroups()");
        System.out.println("--------");
        int counter = 0;
        for(int i = 0; i < completeList.size(); i++){
            counter++;
            System.out.print("This is group nb: " + counter);
            System.out.println("It consists of following bodyweights representing participants: ");
            for(Participant p : completeList.get(i)){
                System.out.println(p.getBodyWeight());
            }
        }

        return completeList;
    }

    private void divideParticipantsByGenderAndWeight(List<Participant> list){
        for(Participant p : list){
            if(p.getGender().equals(Lifter.Gender.MALE)){
                addParticipantToMensGroup(p);
            } else {
                addParticipantToWomensGroup(p);
            }
        }
    }

    /**
     * Gets the groupnumber based on the index in the respective weightclass arrays
     * based on the participant's bodyweight. Also distinguishes between gender.
     * @param p Participant
     * @return An group number (int) based on the index in the weightclasses array
     */
    private int getIndexByBodyWeight(Participant p){
        double bodyWeight = p.getBodyWeight();

        if (p.getGender() == Lifter.Gender.MALE) {
            if (bodyWeight <= weightClassesMen[0]) {
                return 1;
            } else if (bodyWeight >= weightClassesMen[weightClassesMen.length - 1]) {
                return weightClassesMen.length;
            } else {
                for (int i = 0; i < weightClassesMen.length - 1; i++) {
                    if (bodyWeight >= weightClassesMen[i] && bodyWeight < weightClassesMen[i + 1]) {
                        return i + 1;
                    }
                }
            }
        } else {
            if (bodyWeight <= weightClassesWomen[0]) {
                return 1;
            } else if (bodyWeight >= weightClassesWomen[weightClassesWomen.length - 1]) {
                return weightClassesWomen.length;
            } else {
                for (int i = 0; i < weightClassesWomen.length - 1; i++) {
                    if (bodyWeight > weightClassesWomen[i] && bodyWeight < weightClassesWomen[i + 1]) {
                        return i + 1;
                    }
                }
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
    private boolean checkKeyExistsMen(Participant p) {
        if(this.groupsMen.containsKey(getIndexByBodyWeight(p))){
            return true;
        }
        return false;
    }

    /**
     * Predicate method
     * @param p
     * @return True if the key exists in the HashMap, False otherwise
     */
    private boolean checkKeyExistsWomen (Participant p) {
        if(this.groupsMen.containsKey(getIndexByBodyWeight(p))){
            return true;
        }
        return false;
    }

    /**
     * Adds a participant to a weight group represented in the HashMap.
     * If the weight group exists, add partcipant. If not, add new weight group.
     * @param p
     */
    private void addParticipantToMensGroup(Participant p) {
        if(checkKeyExistsMen(p)){
            this.groupsMen.get(getIndexByBodyWeight(p)).add(p);
        } else {
            this.groupsMen.put(getIndexByBodyWeight(p), new ArrayList<Participant>());
            this.groupsMen.get(getIndexByBodyWeight(p)).add(p);
        }
    }

    /**
     * Adds a participant to a weight group represented in the HashMap.
     * If the weight group exists, add partcipant. If not, add new weight group.
     * @param p
     */
    private void addParticipantToWomensGroup(Participant p) {
        if(checkKeyExistsWomen(p)){
            this.groupsWomen.get(getIndexByBodyWeight(p)).add(p);
        } else {
            this.groupsWomen.put(getIndexByBodyWeight(p), new ArrayList<Participant>());
            this.groupsWomen.get(getIndexByBodyWeight(p)).add(p);
        }
    }

    /**
     * Auxiliary method which sorts an ArrayList of Participant according to their body weight placing the
     * participant with the lowest weight first.
     * @param list
     * @return
     */
    public ArrayList<Participant> sortList(ArrayList<Participant> list){
        Collections.sort(list, (p1, p2) -> (int) p1.getBodyWeight() - (int) p2.getBodyWeight());
        return list;
    }

    /**
     * Only works for Mens groups so far.
     * @return
     */
    public List<List<Participant>> createSubgroups(ArrayList<Participant> listOfParticipants) {
        // The list to be populated and returned
        List<List<Participant>> list = new ArrayList<>();

        // Sorted list
        ArrayList<Participant> sortedList = sortList(listOfParticipants);

        int j = 0;

        // For every tenth participant, a new list is made so that every list will contain a max of
        // ten participants.
        for (int i = 1; i < sortedList.size(); i++){
            if (i % 10 == 0) {
                j = i;
                list.add(sortedList
                        .subList(i-10, i)
                );
            }
        }
        // The remaining participants are allocated to a list
        list.add(sortedList
                .subList(j, sortedList.size())
        );

        return list;
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

    @Override
    /**
     * Calculates a specific participant's rank
     * @param participant the participant for whom the ranking is being calculated
     * @return the passed participant's rank
     */
    public int getRank(Participant participant){
        return calculateRankings().indexOf(participant) + 1;
    }

}

