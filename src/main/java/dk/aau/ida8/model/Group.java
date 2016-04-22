package dk.aau.ida8.model;


import dk.aau.ida8.model.groupComparators.CompetingComparator;
import dk.aau.ida8.model.groupComparators.SinclairRankingComparator;
import dk.aau.ida8.model.groupComparators.TotalWeightRankingComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group {

    private List<Participant> participants;
    private Comparator<Participant> groupComparator;
    private Lifter.Gender groupGender;
    private CompetitionTotalWeight.WEIGHTCLASS weightClass;

    public static Group competingGroup(List<Participant> participants) {
        return new Group(participants, new CompetingComparator());
    }

    public static Group sinclairRankingGroup(List<Participant> participants) {
        return new Group(participants, new SinclairRankingComparator());
    }

    public static Group totalWeightRankingGroup(Lifter.Gender gender,
                                                CompetitionTotalWeight.WEIGHTCLASS weightclass) {
        return new Group(
                new TotalWeightRankingComparator(),
                gender,
                weightclass);
    }

    private Group(List<Participant> participants,
                  Comparator<Participant> comparator) {
        this.participants = participants;
        this.groupComparator = comparator;
    }

    private Group(Comparator<Participant> comparator,
                  Lifter.Gender gender,
                  CompetitionTotalWeight.WEIGHTCLASS weightclass) {
        this.participants = participants;
        this.groupComparator = comparator;
        this.groupGender = gender;
        this.weightClass = weightclass;
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

    public Lifter.Gender getGroupGender() {
        if(!this.groupGender.equals(null)) {
            return groupGender;
        } else {
            return this.getParticipants().get(0).getGender();
        }
    }

    public CompetitionTotalWeight.WEIGHTCLASS getWeightClass() {
        return weightClass;
    }

    public void addParticipant(Participant p){
        getParticipants().add(p);
    }
}
