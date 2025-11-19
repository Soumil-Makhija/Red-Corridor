package com.team12.redCorridor.entities;

/**
 * Represents a trap
 */
public class Trap {
    private static final int DAMAGE = 50;

    private final Position position;

    public Trap(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getDamage() {
        return DAMAGE;
    }
}
