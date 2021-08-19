package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGet() {
        String str = "some result";
        future.resolve(str);
        assertEquals(future.get(), str);
    }

    @Test
    public void testResolve() {
        assertFalse(future.isDone());
        String str = "some result";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(future.get(), str);
    }

    @Test
    void testIsDone() {
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
    }

    @Test
    void testGetWithTimeout() throws InterruptedException {
        assertFalse(future.isDone());
        future.get(100, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        String str = "some result";
        future.resolve(str);
        assertEquals(future.get(100, TimeUnit.MILLISECONDS),str);
    }
}
