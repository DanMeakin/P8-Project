package dk.aau.ida8.model;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group {

    private List<Participant> participants;
    private Comparator<Participant> groupComparator;

    public Group competingGroup(List<Participant> participants) {
        return new Group(participants, new CompetingComparator());
    }
    private Lifter.Gender genderOfGroup;
    private CompetitionTotalWeight.WEIGHTCLASS weightClass;

    public Group sinclairRankingGroup(List<Participant> participants) {
        return new Group(participants, new SinclairRankingComparator());
    }

    public Group totalWeightRankingGroup(List<Participant> participants) {
        return new Group(participants, new TotalWeightRankingComparator());
    }

    private Group(List<Participant> participants,
                 Comparator<Participant> comparator) {
        this.participants = participants;
        this.groupComparator = comparator;
    }

    public Lifter.Gender getGroupGender() {
        return getParticipants().get(0).getGender();
    }

    @Override
    public String toString() {
        return "Group: " + getParticipantsCount() + " " +
                getParticipants().get(0).getGender() + " participants";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Group) {
            return equals((Group) o);
        } else {
            return false;
        }
    }

    public boolean equals(Group g) {
        if (this.getParticipantsCount() != g.getParticipantsCount()) {
            return false;
        } else {
            this.sortParticipants();
            g.sortParticipants();
            if (this.getParticipants().equals(g.getParticipants())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public int getParticipantsCount() {
        return participants.size();
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
     */
    public void sortParticipants() {
        Collections.sort(getParticipants(), getGroupComparator());
    }

    public Comparator<Participant> getGroupComparator() {
        return groupComparator;
    }
}
