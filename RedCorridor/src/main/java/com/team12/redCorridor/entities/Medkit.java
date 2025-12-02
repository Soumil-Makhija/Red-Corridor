package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Represents a Healing item (medkit).
 */
public class Medkit {
    // How much health the medkit restores when used
    private int healAmount;

    /**
     * Creates a medkit at the given position.
     * 
     * @param position medkit position
     */
    public Medkit(Position position) {
        // The healing amount is stored in the Constants class
        this.healAmount = Constants.MEDKIT_HEAL_AMOUNT;
    }

    /**
     * Gets the healing amount of the medkit.
     */
    public int getHealAmount() {
        return healAmount;
    }
}
