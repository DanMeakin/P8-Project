package dk.aau.ida8.model;

import javax.persistence.Entity;
import java.util.*;

/**
 *
 */


@Entity
public class CompetitionTotalWeight extends Competition {

    private double[] weightClassesMen = {56, 62, 69, 77, 85, 94, 105};
    private double[] weightClassesWomen = {48, 53, 58, 63, 69, 75};

    private List<Participant> groupsMen;
    private List<Participant> groupsWomen;

    public CompetitionTotalWeight() {

    }

    public CompetitionTotalWeight(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants){
        super(competitionName, host, location, competitionType, date, lastRegistrationDate, maxNumParticipants);
        this.groupsMen = new ArrayList<>();
        this.groupsWomen = new ArrayList<>();
    }

    /**
     * This!
     *
     */
    @Override
    public List<List<Participant>> allocateGroups() {
        List<List<Participant>> completeList = new ArrayList<>();

        // 1st step - divide by gender
        divideParticipantsByGender(getParticipants());

        // 2nd step - divide by weight class
        List<List<Participant>> menListSorted = divideParticipantsByWeight(groupsMen, Lifter.Gender.MALE);
        List<List<Participant>> womenListSorted = divideParticipantsByWeight(groupsWomen, Lifter.Gender.FEMALE);

        // 3rd step - divide into subgroups of a max of 10 participants in each group
        // &
        // 4th step - gather all subgroups to a one list where females come before men and the lightest weight classes
        // comes first.
        for (List<Participant> l : womenListSorted) {
            List<List<Participant>> subList = splitListIntoSubGroups(l);
            for(List<Participant> lp : subList){
                if(lp.size() > 0) {
                    completeList.add(lp);
                }
            }
        }

        for (List<Participant> l : menListSorted) {
                List<List<Participant>> subList = splitListIntoSubGroups(l);
            for(List<Participant> lp : subList){
                if(lp.size() > 0) {
                    completeList.add(lp);
                }
            }
        }





        /*
        for(ArrayList<Participant> list : groupsWomen){
            // Creating a list of subgroups that each has a max of 10 participants
            List<List<Participant>> newList = createSubgroups(list);
            // Each subgroup is added sequentially to the completeList
            for(List<Participant> lp : newList){
                completeList.add(lp);
                //System.out.println(lp + " was added to the complete list.");
            }
        }

        // Looping through the HashMap for lists of participants based on weight class and gender
        for(ArrayList<Participant> list : groupsMen){
            // Creating a list of subgroups that each has a max of 10 participants
            List<List<Participant>> newList = createSubgroups(list);
            // Each subgroup is added sequentially to the completeList
            for(List<Participant> lp : newList){
                completeList.add(lp);
                //System.out.println(lp + " was added to the complete list.");
            }
        }

        //Collections.sort(completeList, (l1,l2) -> l1.get(0).getStartingWeight() - l2.get(0).getStartingWeight());
        */
        /**
         * Printing to console for testing
         */
        /*
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
        }*/

        return completeList;
    }

    public void divideParticipantsByGender(List<Participant> list){
        for(Participant p : list){
            if(p.getGender().equals(Lifter.Gender.MALE)){
                addParticipantToMensGroup(p);
            } else {
                addParticipantToWomensGroup(p);
            }
        }
    }

    private List<List<Participant>> divideParticipantsByWeight(List<Participant> list, Lifter.Gender gender){
        List<List<Participant>> sortedList = new ArrayList<>();

        switch(gender){
            case MALE:
                for(int i = 0; i < this.weightClassesMen.length; i++){
                    sortedList.add(new ArrayList<>());
                }
                    for(Participant p : list){
                        int index = getWeightClassIndex(p, weightClassesMen);
                            if (index >= 0) {
                                //System.out.println("i: " + index + ". Bodyweight is checked if < " + weightClassesMen[index]);
                                sortedList.get(index - 1).add(p);
                            } else if(index == -2){
                                sortedList.get(sortedList.size()-1).add(p);
                            }
                    }
                break;

            case FEMALE:
                for(int i = 0; i < this.weightClassesWomen.length; i++) {
                    sortedList.add(new ArrayList<>());
                }
                for(Participant p : list){
                    int index = getWeightClassIndex(p, weightClassesWomen);
                    if (index >= 0) {
                        //System.out.println("i: " + index + ". Bodyweight is checked if < " + weightClassesWomen[index]);
                        sortedList.get(index - 1).add(p);
                    } else if(index == -2){
                        sortedList.get(sortedList.size()-1).add(p);
                    }
                }
                break;
            default:
                // Throw exception
        }

        System.out.println();
        for(List<Participant> l : sortedList){
            //System.out.println("List object: " + l + " contains:");
            for(Participant p : l){
                //System.out.println("Participant gender: " + p.getGender() + " and weight: " + p.getBodyWeight());
            }
        }
        System.out.println();
        return sortedList;
    }

    /**
     * Returns the index of the weight class.
     *
     * @param p The participant whose weight class is to be found
     * @param weightclass Array of weight classes
     * @return Index of a weight class
     */
    private int getWeightClassIndex(Participant p, double[] weightclass){
        for(int i = 1; i < this.weightClassesMen.length; i++) {
            if (p.getBodyWeight() < weightclass[i]) {
                return i;
            } else if (p.getBodyWeight() > weightclass[weightclass.length-1]){
                return -2;
            }
        }
        return -1;
    }

    private void addParticipantToMensGroup(Participant p) {
            this.groupsMen.add(p);
    }


    private void addParticipantToWomensGroup(Participant p) {
            this.groupsWomen.add(p);
    }

    /**
     * Auxiliary method which sorts an ArrayList of Participant according to their body weight placing the
     * participant with the lowest weight first.
     * @param list
     * @return
     */
    private ArrayList<Participant> sortList(ArrayList<Participant> list){
        Collections.sort(list, (p1, p2) -> (int) p1.getBodyWeight() - (int) p2.getBodyWeight());
        return list;
    }
/*
    private List<List<Participant>> createSubgroups(ArrayList<Participant> listOfParticipants) {
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

        // Check if possible to create a list
        if((j - sortedList.size() - 1) > 0) {
            list.add(sortedList
                    .subList(j, sortedList.size()));
        }

        return list;
    }*/

    /**
     * Splits a supplied list into a list of lists corresponding to subgroups of the total number of participants
     * in the competition.
     * @param list Takes a list of participants of one or the other gender.
     * @return List of lists of participants reflecting subgroups of the competition
     */
    private List<List<Participant>> splitListIntoSubGroups(List<Participant> list) {
        List<List<Participant>> finalList = new ArrayList<>();
        int remainder = list.size() % 10;
        int totalSizeOfList = list.size();
        int chunk = 10;

        if (totalSizeOfList <= 10) {
            // if under 10 participants, just add to the list
            finalList.add(list);
            return finalList;
        } else {
            // add participants in groups of 10
            for (int i = 0; i < totalSizeOfList - remainder; i = i + chunk) {
                finalList.add(list.subList(
                        i, i + chunk
                ));
            }

            // check if there is a remainder. If there is, add the missing participants
            if (remainder > 0) {
                // add the remaining participants
                finalList.add(list.subList(
                        (totalSizeOfList - remainder), totalSizeOfList
                ));
            }

            return finalList;
        }
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

