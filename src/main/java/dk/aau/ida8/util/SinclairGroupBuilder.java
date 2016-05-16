package dk.aau.ida8.util;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;
import dk.aau.ida8.model.Participant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SinclairGroupBuilder extends GroupBuilder {

    public SinclairGroupBuilder(Competition competition) {
        super(competition);
    }

    public List<Group> createRankingGroups() {
        List<Group> groups = new ArrayList<>();
        groupParticipants(Participant::getGender)
                .forEach(grp -> groups.add(Group.sinclairRankingGroup(getCompetition(), grp)));
        Collections.sort(groups, compareFirstByGender(groupingComparator()));
        return groups;
    }

    private Comparator<Group> groupingComparator() {
        return (g1, g2) -> g1.getParticipants().get(0).getStartingSnatchWeight() -
                            g2.getParticipants().get(0).getStartingSnatchWeight();
    }

}
