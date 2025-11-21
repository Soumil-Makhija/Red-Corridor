package com.team12.redCorridor.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorldAccessorTest {

    @Test
    public void uninitializedHealthIsZero() {
        World world =  new World();
        assertEquals(world.hp(), 0);
    }
    @Test
    public void uninitializedMedkitCountIsZero() {
        World world =  new World();
        assertEquals(world.medkits(), 0);
    }
    @Test
    public void zeroFragmentsWhenPlayerNotInitialized() {
        World world =  new World();
        assertEquals(world.fragmentsCollected(), 0);
    }
    @Test
    public void elapsedMillisIsZeroBeforeGameStart() {
        World world =  new World();
        assertEquals(world.elapsedMillis(), 0);
    }
}
