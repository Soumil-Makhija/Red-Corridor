package com.team12.redCorridor.ui.controllers;

/**
 * Immutable snapshot of stats to display on win/defeat scenes.
 */
public class SummaryStats {
    private final int fragmentsCollected;
    private final int fragmentsRequired;
    private final int dronesTriggered;
    private final int trapsTriggered;
    private final long elapsedMillis;
    private final int healthRemaining;

    public SummaryStats(int fragmentsCollected, int fragmentsRequired,
            int dronesTriggered, int trapsTriggered, long elapsedMillis, int healthRemaining) {
        this.fragmentsCollected = fragmentsCollected;
        this.fragmentsRequired = fragmentsRequired;
        this.dronesTriggered = dronesTriggered;
        this.trapsTriggered = trapsTriggered;
        this.elapsedMillis = elapsedMillis;
        this.healthRemaining = healthRemaining;
    }

    public int getFragmentsCollected() {
        return fragmentsCollected;
    }

    public int getFragmentsRequired() {
        return fragmentsRequired;
    }

    public int getDronesTriggered() {
        return dronesTriggered;
    }

    public int getTrapsTriggered() {
        return trapsTriggered;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public int getHealthRemaining() {
        return healthRemaining;
    }
}
