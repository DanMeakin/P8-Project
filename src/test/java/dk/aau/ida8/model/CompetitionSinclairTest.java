package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static dk.aau.ida8.model.Lifter.Gender.MALE;
import static org.junit.Assert.*;

/**
 * Created by mikkelmoerch on 12/04/16.
 */
public class CompetitionSinclairTest extends Competition {

    ArrayList<Participant> list;
    ArrayList<Participant> correctList;
    CompetitionSinclair c;

    Club jyden;

    Lifter l;
    Lifter l2;

    @Before
    public void setUp() throws Exception {
        // Compare list
        list = new ArrayList<>();
        // Competition object, which calculateRankings will be called upon
        c = new CompetitionSinclair();

        jyden = new Club("AK Jyden", new Address());

        l = new Lifter("Brian", "Jensen", jyden, Lifter.Gender.MALE, LocalDate.now(), 80.50);
        l2 = new Lifter("Mark", "Jensen", jyden, Lifter.Gender.MALE, LocalDate.now(), 80.50);

        /*
        Participant p1 = new Participant(l, c, 70);
        Participant p2 = new Participant(l2, c, 71);

        p1.setCurrentWeight(70);
        p1.addLift(Lift.LiftType.SNATCH, true);

        p1.setCurrentWeight(75);
        p1.addLift(Lift.LiftType.CLEANANDJERK, true);

        p2.setCurrentWeight(69);
        p2.addLift(Lift.LiftType.SNATCH, true);

        p2.setCurrentWeight(73);
        p2.addLift(Lift.LiftType.CLEANANDJERK, true);
*/
        // Adding p1 and p2 to the compare list


        // Updating the competition list so its equal to the compare list
        c.addParticipant(l, 70);
        c.addParticipant(l2, 71);

        c.selectParticipationByLifter(l).setCurrentWeight(70);
        c.selectParticipationByLifter(l).addLift(Lift.LiftType.SNATCH, true);

        c.selectParticipationByLifter(l).setCurrentWeight(75);
        c.selectParticipationByLifter(l).addLift(Lift.LiftType.CLEANANDJERK, true);

        c.selectParticipationByLifter(l2).setCurrentWeight(69);
        c.selectParticipationByLifter(l2).addLift(Lift.LiftType.SNATCH, true);

        c.selectParticipationByLifter(l2).setCurrentWeight(73);
        c.selectParticipationByLifter(l2).addLift(Lift.LiftType.CLEANANDJERK, true);

        Participant p1 = c.selectParticipationByLifter(l);
        Participant p2 = c.selectParticipationByLifter(l2);
        list.add(p2);
        list.add(p1);

        correctList = new ArrayList<>();
        correctList.add(p1);
        correctList.add(p2);
    }

    @Test
    public void testCalculateRankings() throws Exception {
        ArrayList<Participant> pList = (ArrayList<Participant>) c.calculateRankings();

        assertNotEquals(list, pList);

        assertEquals(correctList, pList);

    }

}