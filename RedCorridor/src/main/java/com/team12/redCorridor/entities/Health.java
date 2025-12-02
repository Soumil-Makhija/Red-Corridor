/**
 * Stores and manages health value and max health.
 */
package com.team12.redCorridor.entities;

public class Health {
    private int value;
    private final int max;

    /**
     * Creates a Health object with the given max value.
     * 
     * @param max maximum health
     */
    public Health(int max) {
        this.value = max;
        this.max = max;
    }

    /**
     * Increases health by the given amount, up to max.
     * 
     * @param amount amount to increase
     */
    public void increase(int amount) {
        value = Math.min(max, value + amount);
    }

    /**
     * Decreases health by the given amount, not below zero.
     * 
     * @param amount amount to decrease
     */
    public void decrease(int amount) {
        value = Math.max(0, value - amount);
    }

    /**
     * Gets the current health value.
     * 
     * @return health value
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the maximum health value.
     * 
     * @return max health
     */
    public int getMax() {
        return max;
    }
}
