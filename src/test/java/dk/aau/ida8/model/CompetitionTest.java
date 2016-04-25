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
            when(p.isCheckedIn()).thenReturn(true);

            Participant p2 = mock(Participant.class);
            when(p2.getGender()).thenReturn(FEMALE);
            when(p2.getStartingSnatchWeight()).thenReturn(40+i*3);
            when(p.isCheckedIn()).thenReturn(true);

            competition.addParticipant(p);
            competition.addParticipant(p2);
        }
    }

    @Test
    public void finishWeighIn() throws Exception {
        assertTrue(competition.getCompetingGroups().isEmpty());
        assertTrue(competition.getRankingGroups().isEmpty());

        competition.finishWeighIn();

        assertFalse(competition.getCompetingGroups().isEmpty());
        assertFalse(competition.getRankingGroups().isEmpty());
    }

    @Test
    public void finishWeighInWithRemovedParticipants() throws Exception {
        assertTrue(competition.getCompetingGroups().isEmpty());
        assertTrue(competition.getRankingGroups().isEmpty());

        Participant p = mock(Participant.class);
        when(p.isNotCheckedIn()).thenReturn(true);

        int numParticipantsBeforeAdd = competition.getParticipants().size();
        competition.addParticipant(p);

        competition.finishWeighIn();

        assertFalse(competition.getCompetingGroups().isEmpty());
        assertFalse(competition.getRankingGroups().isEmpty());
        assertEquals(numParticipantsBeforeAdd, competition.getParticipants().size());
    }

    @Test
    public void currentParticipant() throws Exception {
        competition.finishWeighIn();
        assertTrue(competition.currentParticipant().isPresent());
        Participant firstParticipant = competition.getCompetingGroups().get(0).getFirstParticipant();
        assertEquals(firstParticipant, competition.currentParticipant().get());
    }

    @Test
    public void getCurrentCompetingGroup() throws Exception {
        competition.finishWeighIn();
        assertTrue(competition.getCurrentCompetingGroup().isPresent());
        Group firstGroup = competition.getCompetingGroups().get(0);
        assertEquals(firstGroup, competition.getCurrentCompetingGroup().get());
    }

    @Test
    public void getCurrentRankingGroup() throws Exception {
        competition.finishWeighIn();
        assertTrue(competition.getCurrentRankingGroup().isPresent());
        Group firstGroup = competition.getRankingGroups().get(0);
        assertEquals(firstGroup, competition.getCurrentRankingGroup().get());
    }

}