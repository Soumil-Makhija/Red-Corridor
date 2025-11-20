package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameModeTest {

    @Test
    public void eachModeHasConsistentConfiguration() {
        for (GameMode mode : GameMode.values()) {
            assertNotNull(mode.displayName());
            assertTrue(mode.fragments() >= 0);
            assertTrue(mode.medkits()   >= 0);
            assertTrue(mode.traps()     >= 0);
            assertTrue(mode.droneRooms() >= 0);
            assertTrue(mode.emptyRooms() >= 0);
            assertTrue(mode.drones()     >= 0);
        }
    }

    @Test
    public void easyModeHasExpectedPresetValues() {
        GameMode easy = GameMode.EASY;

        assertEquals("Easy", easy.displayName());
        assertEquals(6, easy.fragments());
        assertEquals(6, easy.medkits());
        assertEquals(2, easy.traps());
        assertEquals(6, easy.droneRooms());
        assertEquals(0, easy.emptyRooms());
        assertEquals(3, easy.drones());
    }
}
