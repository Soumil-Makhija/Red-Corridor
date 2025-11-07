package com.team12.redCorridor.engine;

/**
 * Represents high-level game states. Engine and UI can use this to decide
 * which logic / rendering to run.
 */
public enum GameState {
    MENU,       // main menu
    PLAYING,    // normal gameplay
    PAUSED,     // paused state
    GAME_OVER,  // player lost
    WIN         // player won
}