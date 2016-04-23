package dk.aau.ida8.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.datatransfer.FlavorEvent;
import java.util.Date;

import static dk.aau.ida8.model.Lifter.Gender.FEMALE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by daniel on 23/04/16.
 */
public class CompetitionTest {

    private Competition competition;

    @Before
    public void setUpBefore() throws Exception {
        competition = new Competition("Test Competition",
                mock(Club.class),
                mock(Address.class),
                Competition.CompetitionType.SINCLAIR,
                mock(Date.class),
                mock(Date.class),
                50
                );
        for (int i = 0; i < 10; i++) {
            Participant p = mock(Participant.class);
            when(p.getGender()).thenReturn(Lifter.Gender.MALE);
            when(p.getStartingSnatchWeight()).thenReturn(50+i*10);

            Participant p2 = mock(Participant.class);
            when(p2.getGender()).thenReturn(FEMALE);
            when(p2.getStartingSnatchWeight()).thenReturn(40+i*3);

            competition.addParticipant(p);
            competition.addParticipant(p2);
        }
    }

    @Test
    public void allocateGroups() throws Exception {
        assertNull(competition.getCompetingGroups());
        assertNull(competition.getRankingGroups());

        competition.allocateGroups();

        assertFalse(competition.getCompetingGroups().isEmpty());
        assertFalse(competition.getRankingGroups().isEmpty());
    }

    @Test
    public void currentParticipant() throws Exception {
        competition.allocateGroups();
        assertTrue(competition.currentParticipant().isPresent());
        Participant firstParticipant = competition.getCompetingGroups().get(0).getFirstParticipant();
        assertEquals(firstParticipant, competition.currentParticipant().get());
    }

    @Test
    public void getCurrentCompetingGroup() throws Exception {
        competition.allocateGroups();
        assertTrue(competition.getCurrentCompetingGroup().isPresent());
        Group firstGroup = competition.getCompetingGroups().get(0);
        assertEquals(firstGroup, competition.getCurrentCompetingGroup().get());
    }

    @Test
    public void getCurrentRankingGroup() throws Exception {
        competition.allocateGroups();
        assertTrue(competition.getCurrentRankingGroup().isPresent());
        Group firstGroup = competition.getRankingGroups().get(0);
        assertEquals(firstGroup, competition.getCurrentRankingGroup().get());
    }

}