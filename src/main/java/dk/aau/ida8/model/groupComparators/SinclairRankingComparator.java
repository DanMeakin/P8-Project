package dk.aau.ida8.model.groupComparators;

import dk.aau.ida8.model.Participant;

import java.util.Comparator;

public class SinclairRankingComparator implements Comparator<Participant> {

    @Override
    public int compare(Participant p1, Participant p2) {
        return (int) (p2.getSinclairScore() - p1.getSinclairScore());
    }

}
