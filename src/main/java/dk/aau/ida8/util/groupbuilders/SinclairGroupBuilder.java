package dk.aau.ida8.util.groupbuilders;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Participant;

import java.util.Comparator;
import java.util.function.Function;

public class SinclairGroupBuilder extends GroupBuilder {

    public SinclairGroupBuilder(Competition competition) {
        super(competition);
    }

    @Override
    Function<Participant, Object> getRankingGrouper() {
        return Participant::getGender;
    }

    @Override
    Group.ComparatorType getRankingComparatorType() {
        return Group.ComparatorType.SINCLAIR_RANKING;
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
        return (g1, g2) -> g1.getParticipants().get(0).getStartingSnatchWeight() -
                            g2.getParticipants().get(0).getStartingSnatchWeight();
    }

}