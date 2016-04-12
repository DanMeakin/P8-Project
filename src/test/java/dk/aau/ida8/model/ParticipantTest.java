package dk.aau.ida8.model;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.Assert.*;

public class ParticipantTest {

    private static Lifter lifter;
    private static Competition competition;
    private static ScoreStrategy scoreStrat;
    private static List<Lift> lifts;
    private static Participant participant;

    public static double expectedScore = 35.0;

    @BeforeClass
    public static void setUpClass() {
        lifter = mock(Lifter.class);
        competition = mock(Competition.class);
        participant = new Participant(lifter, competition, 10);
        participant.addPassedLift();
        participant.setCurrentWeight(15);
        participant.addPassedLift();
        participant.setCurrentWeight(20);
        participant.addFailedLift();
        participant.addPassedLift();
        participant.setCurrentWeight(25);
        participant.addFailedLift();
        participant.addFailedLift();
        when(competition.calculateScore(participant)).thenReturn(expectedScore);
    }

    @Test
    public void testGetTotalScore() throws Exception {
        assertEquals(expectedScore, participant.getTotalScore(), 0.001);
    }

    @Test
    public void testGetBestCleanAndJerk() throws Exception {
        assertEquals(20, participant.getBestCleanAndJerk());
    }

    @Test
    public void testGetBestSnatch() throws Exception {
        assertEquals(15, participant.getBestSnatch());
    }
}