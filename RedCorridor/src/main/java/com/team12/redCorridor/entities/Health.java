// Value object to encapsulate health logic and max health
package com.team12.redCorridor.entities;

public class Health {
    private int value;
    private final int max;

    public Health(int max) {
        this.value = max;
        this.max = max;
    }

    public void increase(int amount) {
        value = Math.min(max, value + amount);
    }

    public void decrease(int amount) {
        value = Math.max(0, value - amount);
    }

    public int getValue() {
        return value;
    }

    public int getMax() {
        return max;
    }
}
