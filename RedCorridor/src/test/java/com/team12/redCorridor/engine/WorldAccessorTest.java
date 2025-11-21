package com.team12.redCorridor.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorldAccessorTest {

    @Test
    public void uninitializedHealthIsZero() {
        World world =  new World();
        assertEquals(0, world.hp());
    }
    @Test
    public void uninitializedMedkitCountIsZero() {
        World world =  new World();
        assertEquals(0, world.medkits());
    }
    @Test
    public void zeroFragmentsWhenPlayerNotInitialized() {
        World world =  new World();
        assertEquals(0, world.fragmentsCollected());
    }
    @Test
    public void elapsedMillisIsZeroBeforeGameStart() {
        World world =  new World();
        assertEquals(0, world.elapsedMillis());
    }
}
