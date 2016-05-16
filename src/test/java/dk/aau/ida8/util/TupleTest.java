package dk.aau.ida8.util;

import dk.aau.ida8.model.Lifter;
import org.junit.Test;

import static org.junit.Assert.*;

public class TupleTest {

    @Test
    public void tupleEquals() throws Exception {
        Tuple<Lifter.Gender, Integer> t1 = new Tuple<>(Lifter.Gender.FEMALE, 1);
        Tuple<Lifter.Gender, Integer> t2 = new Tuple<>(Lifter.Gender.FEMALE, 1);
        Tuple<Lifter.Gender, Integer> t3 = new Tuple<>(Lifter.Gender.FEMALE, 2);
        Tuple<Lifter.Gender, Integer> t4 = new Tuple<>(Lifter.Gender.MALE, 1);

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, t4);
    }

}