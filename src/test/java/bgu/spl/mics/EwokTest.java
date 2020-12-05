package bgu.spl.mics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EwokTest {
    //Object under test
    Ewok e = new Ewok();

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
