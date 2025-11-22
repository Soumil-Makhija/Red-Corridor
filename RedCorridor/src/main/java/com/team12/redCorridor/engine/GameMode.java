package com.team12.redCorridor.engine;

/**
 * Mode presets
 */
public enum GameMode {
    EASY("Easy", 6, 6, 2, 6, 0, 3),
    MEDIUM("Medium", 6, 6, 2, 5, 4, 5),
    HARD("Hard", 6, 6, 3, 5, 4, 5),
    EXTREME("Extreme", 6, 6, 4, 4, 4, 6);

    private final String displayName;
    private final int fragments;
    private final int medkits;
    private final int traps;
    private final int droneRooms;
    private final int emptyRooms;
    private final int drones;

    GameMode(String displayName, int fragments, int medkits, int traps, int droneRooms, int emptyRooms, int drones) {
        this.displayName = displayName;
        this.fragments = fragments;
        this.medkits = medkits;
        this.traps = traps;
        this.droneRooms = droneRooms;
        this.emptyRooms = emptyRooms;
        this.drones = drones;
    }

    public String displayName() {
        return displayName;
    }

    public int fragments() {
        return fragments;
    }

    public int medkits() {
        return medkits;
    }

    public int traps() {
        return traps;
    }

    public int droneRooms() {
        return droneRooms;
    }

    public int emptyRooms() {
        return emptyRooms;
    }

    public int drones() {
        return drones;
    }
}
