package dk.aau.ida8.model;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;

/**
 *
 */


@Entity
public class CompetitionTotalWeight extends Competition {

    public enum WEIGHTCLASS {
        WEIGHTCLASS1, WEIGHTCLASS2, WEIGHTCLASS3, WEIGHTCLASS4, WEIGHTCLASS5, WEIGHTCLASS6, WEIGHTCLASS7, WEIGHTCLASS8
    }

    private double[] weightClassesMen = {56, 62, 69, 77, 85, 94, 105};
    private double[] weightClassesWomen = {48, 53, 58, 63, 69, 75};

    @Transient
    private List<Participant> groupsMen;

    @Transient
    private List<Participant> groupsWomen;

    private List<Group> rankedGroups;

    public CompetitionTotalWeight() {

    }


    public CompetitionTotalWeight(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants){
        super(competitionName, host, location, competitionType, date, lastRegistrationDate, maxNumParticipants);
        this.groupsMen = new ArrayList<>();
        this.groupsWomen = new ArrayList<>();
        this.rankedGroups = createRankedGroups();
    }

    /**
     * This!
     *
     */
    @Override
    public void allocateGroups() {
        List<Group> completeList = new ArrayList<>();

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
            List<Group> subList = splitListIntoSubGroups(l);
            for(Group g : subList){
                if(g.getParticipantsCount() > 0) {
                    completeList.add(g);
                }
            }
        }

        for (List<Participant> l : menListSorted) {
                List<Group> subList = splitListIntoSubGroups(l);
            for(Group g: subList){
                if(g.getParticipantsCount() > 0) {
                    completeList.add(g);
                }
            }
        }

        setGroupList(completeList);
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

    /**
     * Splits a supplied list into a list of lists corresponding to subgroups of the total number of participants
     * in the competition.
     * @param list Takes a list of participants of one or the other gender.
     * @return List of lists of participants reflecting subgroups of the competition
     */
    private List<Group> splitListIntoSubGroups(List<Participant> list) {
        List<Group> finalList = new ArrayList<>();
        int remainder = list.size() % 10;
        int totalSizeOfList = list.size();
        int chunk = 10;

        if (totalSizeOfList <= 10) {
            // if under 10 participants, just add to the list
            finalList.add(Group.competingGroup(list));
            return finalList;
        } else {
            // add participants in groups of 10
            for (int i = 0; i < totalSizeOfList - remainder; i = i + chunk) {
                finalList.add(Group.competingGroup((list.subList(
                        i, i + chunk
                ))));
            }

            // check if there is a remainder. If there is, add the missing participants
            if (remainder > 0) {
                // add the remaining participants
                finalList.add(Group.competingGroup(list.subList(
                        (totalSizeOfList - remainder), totalSizeOfList
                )));
            }

            return finalList;
        }
    }

    /**
     * Populates ranked groups with participants.
     *
     * This method is to be called once during a competition, after the weight-in
     * and before competition start.
     */
    public void populateRankedGroups(){
        List<Group> allGroups = getGroupList();

        List<Group> tempMenGroups = getGenderSortedGroup(allGroups, Lifter.Gender.MALE);
        List<Group> tempWomenGroups = getGenderSortedGroup(allGroups, Lifter.Gender.FEMALE);

        allocateParticipantsToRankedGroups(tempMenGroups);
        allocateParticipantsToRankedGroups(tempWomenGroups);

        removeEmptyRankedGroups();

        //Initial sort of ranking for each ranked group
        calculateRankings();
    }

    /**
     * Trims the list of ranked groups removing empty groups.
     */
    private void removeEmptyRankedGroups(){
        for(Iterator<Group> g = this.rankedGroups.iterator(); g.hasNext();){
            Group group = g.next();
            if(group.getParticipantsCount() == 0){
                g.remove();
            }
        }
    }

    @Override
    public void calculateRankings() {
        this.rankedGroups.forEach(Group::sortParticipants);
    }

    /**
     * Creates a list of empty ranked groups.
     * One ranked group per gender per weightclass will be added
     * to the list.
     *
     * @return List of non-populated ranked groups
     */
    private List<Group> createRankedGroups(){
        List<Group> rankedGroups = new ArrayList<>();

        List<Group> maleRankedGroup = createRankedGroupsForGender(Lifter.Gender.MALE);
        List<Group> femaleRankedGroup = createRankedGroupsForGender(Lifter.Gender.FEMALE);

        //Adds every group in femaleRankedGroup to rankedGroups
        rankedGroups.addAll(femaleRankedGroup);
        //Same for maleRankedGroup
        rankedGroups.addAll(maleRankedGroup);

        return rankedGroups;
    }

    /**
     * Creates a list with non-populated empty groups for one gender.
     * Creates one group per weightclass and adds it to the list.
     *
     * META-NOTE TO CODE:
     * The method does have a workaround on the foreach loop of every
     * enum weightclass value. The WEIGHTCLASS enum has eight values because
     * men have eight weightclasses. However, women have only seven. Thus,
     * the while loop has been included to limit the number of rankedGroups
     * added to the list to seven.
     *
     * @param gender The general gender of the ranked groups to be created
     * @return List of non-populated ranked groups for a gender
     */
    private List<Group> createRankedGroupsForGender(Lifter.Gender gender){
        List<Group> rankedGroups = new ArrayList<>();
        if(gender.equals(Lifter.Gender.FEMALE)) {
            int counter = 0;
            while(counter < 8) {
                for (WEIGHTCLASS w : WEIGHTCLASS.values()) {
                    counter++;
                    rankedGroups.add(Group.totalWeightRankingGroup(gender, w));
                }
            }
        }
        return rankedGroups;
    }

    /**
     * Allocates participants to the ranked group they belong to based on
     * gender and weightclass.
     *
     * That is a male lifter in weight class 1 would be added to the ranked group
     * for male lifters in weight class 1.
     *
     * @param allGroups List of competition groups
     */
    private void allocateParticipantsToRankedGroups(List<Group> allGroups){
        for(Group subGroup : allGroups){
            for(Group rankedGroup : this.rankedGroups) {
                if (subGroup.getWeightClass().equals(rankedGroup.getWeightClass())) {
                    addParticipantToRankedGroup(subGroup, rankedGroup);
                }
            }
        }
    }

    private void addParticipantToRankedGroup(Group competitiongroup, Group weightclassgroup){
        for(Participant p : competitiongroup.getParticipants()){
            weightclassgroup.addParticipant(p);
        }
    }

    private List<Group> getGenderSortedGroup(List<Group> allGroups, Lifter.Gender gender){
        List<Group> genderSortedGroups = new ArrayList<>();
        for(Group g : allGroups){
            if(g.getGroupGender().equals(gender)){
                genderSortedGroups.add(g);
            }
        }

        return genderSortedGroups;
    }

    @Override
    /**
     * Calculates a specific participant's rank
     * @param participant the participant for whom the ranking is being calculated
     * @return the passed participant's rank
     */
    public int getRank(Participant participant){
        // TODO obsolete method. reimplement to instead provide a ranking based on yet-to-be-implemented ranking groups
        return 1000;
        //return calculateRankings().indexOf(participant) + 1;
    }

}

