package dk.aau.ida8.util.groupcomparators;

import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Participant;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by daniel on 23/05/16.
 */
public class CompetingComparatorTest {

    /**
     * Tests the {@link CompetingComparator#compare(Participant, Participant)}
     * method.
     *
     * To properly test this comparator, we require to mock participants who
     * have completed different numbers of lifts from one another, who are next
     * lifting different weights, who have different numbers of attempts, who
     * have completed lifts with different timestamps, and who have different
     * start numbers.
     *
     * These must reflect the ordering of comparisons in the
     * {@link CompetingComparator#compare(Participant, Participant)} method.
     */
    @Test
    public void compare() throws Exception {
        List<Participant> participants = new ArrayList<>();
        // Create default mocked participants, with no lifts and a lift weight
        // of 50kg.
        List<Lift> emptyLifts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Lift l = mock(Lift.class);
            when(l.getTimestamp()).thenReturn(LocalDateTime.of(2000, 1, 1, 19, 0, 0));
            emptyLifts.add(l);
        }
        for (int i = 0; i < 9; i++) {
            Participant p = mock(Participant.class);
            when(p.getLiftsCount()).thenReturn(0);
            when(p.getCurrentWeight()).thenReturn(50);
            when(p.getLifts()).thenReturn(emptyLifts);
            participants.add(p);
        }

        // Mock participants to be compared based on completions
        // P0 should come last, as having completed all lifts. P2 should come
        // before P1, having fewer attempts. P3 & P4 are equal on this factor.
        when(participants.get(0).getLiftsCount()).thenReturn(6);
        when(participants.get(1).getLiftsCount()).thenReturn(5);
        when(participants.get(2).getLiftsCount()).thenReturn(4);
        when(participants.get(3).getLiftsCount()).thenReturn(2);
        when(participants.get(4).getLiftsCount()).thenReturn(2);

        // Mock participants to be compared based on lift weight
        // P3 should come before P4.
        when(participants.get(3).getCurrentWeight()).thenReturn(60);
        when(participants.get(4).getCurrentWeight()).thenReturn(70);

        // Mock participants to be compared based on timestamp
        // P5 should come before P6
        Lift l1 = mock(Lift.class);
        Lift l2 = mock(Lift.class);
        when(l1.getTimestamp()).thenReturn(LocalDateTime.of(2000, 1, 1, 18, 0, 0));
        when(l2.getTimestamp()).thenReturn(LocalDateTime.of(2000, 1, 1, 18, 0, 1));
        when(participants.get(5).getLiftsCount()).thenReturn(1);
        when(participants.get(6).getLiftsCount()).thenReturn(1);
        when(participants.get(5).getLifts()).thenReturn(Arrays.asList(l1));
        when(participants.get(6).getLifts()).thenReturn(Arrays.asList(l2));

        // Mock participants to be compared based on start number
        // P7 should come before P8
        when(participants.get(7).getStartNumber()).thenReturn(1);
        when(participants.get(8).getStartNumber()).thenReturn(2);

        // Combine all participants in expected order, essentially taking the
        // above factors in reverse.
        List<Participant> expected = Arrays.asList(
                participants.get(7),
                participants.get(8),
                participants.get(5),
                participants.get(6),
                participants.get(3),
                participants.get(4),
                participants.get(2),
                participants.get(1),
                participants.get(0)
        );

        List<Participant> actual = new ArrayList<>(participants);
        actual.sort(new CompetingComparator());
        assertEquals(expected, actual);
    }

}