package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Manages medkit collection and usage for a player.
 */
public class MedkitInventory {
    private int medkits;

    /**
     * Creates an empty medkit inventory.
     */
    public MedkitInventory() {
        this.medkits = 0;
    }

    /**
     * Adds a medkit to the inventory.
     */
    public void collectMedkit() {
        medkits++;
    }

    /**
     * Uses a medkit to heal the given health object.
     * 
     * @param health the Health object to heal
     * @return amount healed, or 0 if no medkits
     */
    public int consumeMedkit(Health health) {
        if (medkits > 0) {
            int before = health.getValue();
            health.increase(Constants.MEDKIT_HEAL_AMOUNT);
            medkits--;
            return health.getValue() - before;
        }
        return 0;
    }

    /**
     * Gets the number of medkits in inventory.
     * 
     * @return medkit count
     */
    public int getMedkitCount() {
        return medkits;
    }
}