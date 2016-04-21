package dk.aau.ida8.model;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class CompetitionSinclair extends Competition {

    public CompetitionSinclair(String competitionName, Club host, Address location, CompetitionType competitionType, Date date, Date lastRegistrationDate, int maxNumParticipants){
            super(competitionName, host, location, competitionType, date, lastRegistrationDate, maxNumParticipants);
    }

    public CompetitionSinclair() {
    }

    /**
     * Allocates groupings for a particular Sinclair competition.
     *
     * The Sinclair competition requires participants to be split into groups
     * based on gender and the participant's chosen snatch starting weight.
     *
     * Each group consists of, at most, ten participants. Each group will only
     * contain lifters of one gender.
     */
    @Override
    public void allocateGroups() {
        List<Participant> bigList = getParticipants();
        List<Group> subGroups;

        // sort the big list based on snatch starting weight
        Collections.sort(bigList, (p1,p2) -> p1.getStartingWeight() - p2.getStartingWeight());

        // split groups up into genders
        List<Participant> listMale = splitListByGender(bigList, Lifter.Gender.MALE);
        List<Participant> listFemale = splitListByGender(bigList, Lifter.Gender.FEMALE);

        // make a list of lists where subgroups can be added
        subGroups = splitListIntoSubGroups(listMale);
        subGroups.addAll(splitListIntoSubGroups(listFemale));
        setGroupList(subGroups);
    }

    /**
     * Splits a supplied list into a list of lists corresponding to sub-groups
     * of the total number of participants in the competition.
     *
     * Each sub-group cannot include more than 10 participants.
     *
     * @param list Takes a list of participants of one or the other gender.
     * @return List of lists of participants reflecting subgroups of the
     *         competition
     */
    private List<Group> splitListIntoSubGroups(List<Participant> list) {
        List<Group> finalList = new ArrayList<>();
        int subGroupSize = 10;

        for (int i = 0; i < list.size(); i = Math.min(i+subGroupSize, list.size())) {
            int endIdx = Math.min(i+subGroupSize, list.size());
            finalList.add(new Group(list.subList(i, endIdx)));
        }

        return finalList;
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
    public List<Participant> calculateRankings() {
        return getParticipants().stream()
                .sorted((p1, p2) -> (int) Math.round(p2.getSinclairScore() - p1.getSinclairScore()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates a specific participant's rank
     * @param participant the participant for whom the ranking is being calculated
     * @return the passed participant's rank
     */
    public int getRank(Participant participant){
        return calculateRankings().indexOf(participant) + 1;
    }
}
