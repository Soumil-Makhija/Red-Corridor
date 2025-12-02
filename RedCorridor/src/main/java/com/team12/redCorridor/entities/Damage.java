/**
 * Stores damage value for an entity.
 */
package com.team12.redCorridor.entities;

public class Damage {
    private final int value;

    /**
     * Creates a Damage object with the given value.
     * 
     * @param value damage amount
     */
    public Damage(int value) {
        this.value = value;
    }

    /**
     * Gets the damage value.
     * 
     * @return damage amount
     */
    public int getValue() {
        return value;
    }
}
