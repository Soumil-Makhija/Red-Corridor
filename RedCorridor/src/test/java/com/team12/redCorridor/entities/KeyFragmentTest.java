package com.team12.redCorridor.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyFragmentTest {

    @Test
    public void keyFragmentStartsNotCollectedAndCanBeCollected() {
        KeyFragment fragment = new KeyFragment(new Position(1, 1));
        assertFalse(fragment.isCollected());

        fragment.collect();
        assertTrue(fragment.isCollected());
    }
}
