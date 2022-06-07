package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    //the field of ewok that we test on it
    private Ewok ewok;

    @BeforeEach
    //preparing the object to the test
    public void setUp(){
        ewok = new Ewok();
    }

    @Test
    void testAcquire() {
        ewok.acquire();
        //test if available field is false
        assertFalse(ewok.getAvailable());
    }

    @Test
    void testRelease() {
        ewok.release();
        //test if the available field id true
        assertTrue(ewok.getAvailable());

    }
    @AfterEach
    public void tearDown(){
        ewok=null;
    }

}
