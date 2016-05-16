package dk.aau.ida8.util;

import dk.aau.ida8.model.Competition;
import dk.aau.ida8.model.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by daniel on 16/05/16.
 */
public class TotalWeightGroupBuilder extends GroupBuilder {

    public TotalWeightGroupBuilder(Competition competition) {
        super(competition);
    }

    public List<Group> createRankingGroups() {
        List<Group> groups = new ArrayList<>();
        groupParticipants(p -> new Tuple<>(p.getGender(), p.getWeightClass()))
                .forEach(grp -> groups.add(Group.totalWeightRankingGroup(getCompetition(), grp)));
        Collections.sort(groups, compareFirstByGender(groupingComparator()));
        return groups;
    }

    private Comparator<Group> groupingComparator() {
        return (g1, g2) -> (int)
                g1.getParticipants().get(0).getWeightClass() -
                        g2.getParticipants().get(0).getWeightClass();
    }

}
