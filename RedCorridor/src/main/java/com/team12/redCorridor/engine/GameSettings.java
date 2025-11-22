package com.team12.redCorridor.engine;

/**
 * Store game settings
 */
public final class GameSettings {
    private static GameMode currentMode = GameMode.MEDIUM; // default

    private GameSettings() {
    }

    public static GameMode getCurrentMode() {
        return currentMode;
    }

    public static void setMode(GameMode mode) {
        if (mode != null) {
            currentMode = mode;
        }
    }
}
