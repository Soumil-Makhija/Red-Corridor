package com.team12.redCorridor.entities;

// This class represents a collectible key fragment in the game
public class KeyFragment {
    // Tells whether the key fragment has been collected or not
    private boolean collected;

    // Constructor: sets where the key fragment is and marks it as not collected yet
    public KeyFragment(Position position) {
        this.collected = false; // starts as not collected
    }

    // Marks this key fragment as collected
    public void collect() {
        collected = true;
    }

    // Returns true if the key fragment has already been collected
    public boolean isCollected() {
        return collected;
    }
}
