package com.team12.redCorridor.ui.controllers;

/**
 * Immutable snapshot of stats to display on win/defeat scenes.
 */
public class SummaryStats {
    private final int fragmentsCollected;
    private final int fragmentsRequired;
    private final int dronesTriggered;
    private final int hazardsTriggered;
    private final long elapsedMillis;
    private final int healthRemaining;

    public SummaryStats(int fragmentsCollected, int fragmentsRequired,
            int dronesTriggered, int hazardsTriggered, long elapsedMillis, int healthRemaining) {
        this.fragmentsCollected = fragmentsCollected;
        this.fragmentsRequired = fragmentsRequired;
        this.dronesTriggered = dronesTriggered;
        this.hazardsTriggered = hazardsTriggered;
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

    public int getHazardsTriggered() {
        return hazardsTriggered;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public int getHealthRemaining() {
        return healthRemaining;
    }
}
