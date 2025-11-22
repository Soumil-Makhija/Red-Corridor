package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

// This class represents a healing item called a Medkit
public class Medkit {
    // How much health the medkit restores when used
    private int healAmount;

    // Constructor: sets where the medkit is and how much it heals
    public Medkit(Position position) {
        // The healing amount is stored in the Constants class
        this.healAmount = Constants.MEDKIT_HEAL_AMOUNT;
    }

    // Returns how much health this medkit can restore
    public int getHealAmount() {
        return healAmount;
    }
}
