package dk.aau.ida8.model;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class CompetitionSinclair extends Competition {

    private transient List<Group> winnerGroups;

    public CompetitionSinclair(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants){
        super(competitionName, host, location, competitionType, date, lastRegistrationDate, maxNumParticipants);
        winnerGroups = new ArrayList<>();
    }

    public CompetitionSinclair() {
    }

    @Override
    public List<Group> allocateGroups() {
        List<Participant> bigList = getParticipants();
        List<Group> subGroupsMen;
        List<Group> subGroupsWomen;

        // sort the big list based on snatch starting weight
        Collections.sort(bigList, (p1,p2) -> p1.getStartingWeight() - p2.getStartingWeight());

        // split groups up into genders
        List<Participant> listMale = splitListByGender(bigList, Lifter.Gender.MALE);
        List<Participant> listFemale = splitListByGender(bigList, Lifter.Gender.FEMALE);

        // split gender groups into participant groups
        subGroupsMen = splitListIntoSubGroups(listMale);
        subGroupsWomen = splitListIntoSubGroups(listFemale);

        // the final list that is to be returned, which is a list concatenated with female and male groups
        List<Group> returnList = new ArrayList<>();

        for (Group subGroupWomen : subGroupsWomen) {
            returnList.add(subGroupWomen);
        }

        for (Group subGroupMen : subGroupsMen) {
            returnList.add(subGroupMen);
        }

        return returnList;
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
            finalList.add(Group.createGroup(list));
            return finalList;
        } else {
            // add participants in groups of 10
            for (int i = 0; i < totalSizeOfList - remainder; i = i + chunk) {
                finalList.add(Group.createGroup(list.subList(
                        i, i + chunk
                )));
            }

            // check if there is a remainder. If there is, add the missing participants
           if (remainder > 0) {
                // add the remaining participants
                finalList.add(Group.createGroup(list.subList(
                        (totalSizeOfList - remainder), totalSizeOfList
                )));
            }

            return finalList;
        }
    }

    private List<Participant> splitListByGender(List<Participant> list, Lifter.Gender gender) {
        return list.stream()
                .filter(p -> p.getGender() == gender)
                .collect(Collectors.toList());
    }

    /**
     * Calculates and returns the rankings for this competition.
     *
     * The ranking uses the score for each participation calculated using the
     * associated ScoreStrategy.
     *
     * @return participants for this competition, ranked in order
     */
    @Override
    public void calculateRankings() {
        List<Participant> listOfAllParticipants = getParticipants();

        List<Group> combinedListOfWinners = new ArrayList<>();

        List<Participant> groupsMen = splitListByGender(listOfAllParticipants, Lifter.Gender.MALE);
        List<Participant> groupsWomen = splitListByGender(listOfAllParticipants, Lifter.Gender.FEMALE);

        combinedListOfWinners.add(Group.createGroup(
                sortListBasedOnSinclairScore(groupsWomen)
        ));
        combinedListOfWinners.add(Group.createGroup(
                sortListBasedOnSinclairScore(groupsMen)
        ));

        winnerGroups = combinedListOfWinners;
    }

    private List<Participant> sortListBasedOnSinclairScore(List<Participant> list){
        return list.stream()
                .sorted((p1,p2) -> (int) Math.round(p2.getSinclairScore() - p1.getSinclairScore()))
                .collect(Collectors.toList());
    }

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
