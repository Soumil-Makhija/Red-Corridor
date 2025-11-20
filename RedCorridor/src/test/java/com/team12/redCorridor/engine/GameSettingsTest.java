package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameSettingsTest {

    @Test
    public void defaultModeIsEasy() {
        
        assertEquals(GameMode.EASY, GameSettings.getCurrentMode());
    }

    @Test
    public void setModeChangesAndIgnoresNull() {
        GameSettings.setMode(GameMode.HARD);
        assertEquals(GameMode.HARD, GameSettings.getCurrentMode());

       
        GameSettings.setMode(null);
        assertEquals(GameMode.HARD, GameSettings.getCurrentMode());


        GameSettings.setMode(GameMode.EASY);
    }
}
