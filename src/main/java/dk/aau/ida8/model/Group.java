package dk.aau.ida8.model;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group {

    private List<Participant> participantList;

    private Lifter.Gender genderOfGroup;
    private CompetitionTotalWeight.WEIGHTCLASS weightClass;

    public Group(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public Group(Lifter.Gender gender, CompetitionTotalWeight.WEIGHTCLASS WEIGHTCLASS){
        this.genderOfGroup = gender;
        this.weightClass = WEIGHTCLASS;
    }

    /**
     * Sorts and returns the list of participants.
     *
     * The identity of the next participant is calculated based on which
     * participant has chosen the lowest weight to lift. If two or more lifters
     * are to be lifting the same weight, the next participant is determined
     * by taking the lowest of these lifters' ID#.
     *
     * @return sorted list of participants
     */
    public List<Participant> determineParticipationOrder() {
        Collections.sort(participantList, new Comparator<Participant>() {
            @Override
            public int compare(Participant p1, Participant p2) {
                int completionComp = 0;
                int weightComp = p1.getCurrentWeight() - p2.getCurrentWeight();
                int attemptsComp = p1.getLiftsCount() - p2.getLiftsCount();
                int timestampComp = 0;

                if (p1.getLiftsCount() > 0 && p2.getLiftsCount() > 0) {
                    if ((p1.getLiftsCount() < 3) && (p2.getLiftsCount() < 3)) {
                        if (p1.getLifts().get(0).getTimeLiftCompleted().isBefore(p2.getLifts().get(0).getTimeLiftCompleted())) {
                            timestampComp = 1;
                        } else {
                            timestampComp = -1;
                        }
                    } else if (p1.getLiftsCount() > 3 && p2.getLiftsCount() > 3) {
                        if (p1.getLifts().get(3).getTimeLiftCompleted().isBefore(p2.getLifts().get(3).getTimeLiftCompleted())) {
                            timestampComp = 1;
                        } else {
                            timestampComp = -1;
                        }
                    }
                }

                long idComp = p1.getLifter().getId() - p2.getLifter().getId();

                if (p1.liftsComplete() || p2.liftsComplete()) {
                    if (p1.liftsComplete()) {
                        completionComp = 1;
                    } else if (p2.liftsComplete()) {
                        completionComp = -1;
                    }
                }

                List<Integer> comparators = Arrays.asList(
                        completionComp,
                        weightComp,
                        attemptsComp,
                        timestampComp,
                        (int) idComp
                );
                for (Integer comparatorValue : comparators) {
                    if (comparatorValue != 0) {
                        return comparatorValue;
                    }
                }
                return 0;
            }
        });
        return participantList;
    }

    public void addParticipant(Participant p){
        this.getParticipantList().add(p);
    }

    public Lifter.Gender getGenderOfGroup() {
        return genderOfGroup;
    }

    public void setGenderOfGroup(Lifter.Gender genderOfGroup) {
        this.genderOfGroup = genderOfGroup;
    }

    public CompetitionTotalWeight.WEIGHTCLASS getWeightClass() {
        return weightClass;
    }

    public void setWeightClass(CompetitionTotalWeight.WEIGHTCLASS weightClass) {
        this.weightClass = weightClass;
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public int getNumberOfParticipants () {
        return participantList.size();
    }

}
