package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Participant;
import dk.aau.ida8.util.Tuple;
import dk.aau.ida8.util.groupbuilders.GroupBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class TotalWeightGroupBuilder extends GroupBuilder {

    public TotalWeightGroupBuilder(Competition competition) {
        super(competition);
    }

    @Override
    Function<Participant, Object> getRankingGrouper() {
        return p -> new Tuple<>(p.getGender(), p.getWeightClass());
    }

    @Override
    Group.ComparatorType getRankingComparatorType() {
        return Group.ComparatorType.TOTAL_WEIGHT_RANKING;
    }

    @Override
    Comparator<Group> getRankingGroupComparator() {
        return compareFirstByGender(groupingComparator());
    }

    @Override
    int getCompetingGroupMaxSize() {
        return 10;
    }

    private Comparator<Group> groupingComparator() {
        return (g1, g2) -> (int)
                g1.getParticipants().get(0).getWeightClass() -
                        g2.getParticipants().get(0).getWeightClass();
    }

}