package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {
    int serialNumber = 1;
    //Object under test
    Ewok e = new Ewok(serialNumber);

    @Test
    void acquireTest(){
        assertTrue(e.acquire());
        assertFalse(e.available);
        assertFalse(e.acquire());
    }

    @Test
    void releaseTest(){
        e.release();
        assertTrue(e.available);
    }
}
