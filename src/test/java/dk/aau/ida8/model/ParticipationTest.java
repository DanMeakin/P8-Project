package dk.aau.ida8.model;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.Assert.*;

public class ParticipationTest {

    private static Lifter lifter;
    private static Competition competition;
    private static ScoreStrategy scoreStrat;
    private static List<Lift> lifts;
    private static Participation participation;

    @BeforeClass
    public static void setUpClass() {
        lifter = mock(Lifter.class);
        competition = mock(Competition.class);
        scoreStrat = mock(ScoreStrategy.class);
        participation = new Participation(lifter, competition, 10);
        participation.addLift(Lift.LiftType.CLEANANDJERK, true);
        participation.setCurrentWeight(15);
        participation.addLift(Lift.LiftType.CLEANANDJERK, true);
        participation.setCurrentWeight(20);
        participation.addLift(Lift.LiftType.CLEANANDJERK, false);
        participation.addLift(Lift.LiftType.SNATCH, true);
        participation.setCurrentWeight(25);
        participation.addLift(Lift.LiftType.SNATCH, false);
        participation.addLift(Lift.LiftType.SNATCH, false);
        when(scoreStrat.calculateScore(participation)).thenReturn(35.0);
        when(competition.getScoreStrategy()).thenReturn(scoreStrat);
    }

    @Test
    public void testGetTotalScore() throws Exception {
        assertEquals(35, participation.getTotalScore(), 0.001);
    }

    @Test
    public void testGetBestCleanAndJerk() throws Exception {
        assertEquals(15, participation.getBestCleanAndJerk());
    }

    @Test
    public void testGetBestSnatch() throws Exception {
        assertEquals(20, participation.getBestSnatch());
    }
}