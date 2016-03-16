package dk.aau.ida8.data;

import dk.aau.ida8.model.Lift;
import dk.aau.ida8.model.Lifter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by folmer on 3/16/16.
 */
public class LifterTest {

    Lifter l;

    @Before
    public void setUp() throws Exception {
        l = new Lifter("Jokum", "JAK", "M", 10, 60.00f, 120);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testNextLift() throws Exception {
        List<Lift> listOfLifts = l.getCleanJerks();
        Lift lift = l.nextLift(listOfLifts);

        assertEquals(120, lift.getWeight());

        l.updateLiftStatus(listOfLifts, 1, true);

        Lift lift2 = l.nextLift(listOfLifts);

        assertEquals(121, lift2.getWeight());

        l.updateLiftStatus(listOfLifts, 2, false);

        Lift lift3 = l.nextLift(listOfLifts);

        assertEquals(121, lift3.getWeight());
        assertNotEquals(lift2, lift3);

    }

    @Test
    public void testUpdateLiftStatus() throws Exception {
        List<Lift> listOfLifts = l.getCleanJerks();
        Lift lift = listOfLifts.get(0);

        assertFalse(lift.isPerformed());
        assertFalse(lift.isAccepted());

        l.updateLiftStatus(listOfLifts, 1, false);

        assertFalse(lift.isAccepted());
        assertTrue(lift.isPerformed());

    }

    @Test
    public void testUpdateLiftWeight() throws Exception {
        List<Lift> listOfLifts = l.getCleanJerks();
        Lift lift = listOfLifts.get(1);

        l.updateLiftWeight(listOfLifts, 2, 76);

        assertEquals(lift.getWeight(), 76);

    }
}