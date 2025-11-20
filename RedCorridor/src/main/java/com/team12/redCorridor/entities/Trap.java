package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Represents a trap
 */
public class Trap {
    private final Position position;

    public Trap(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getDamage() {
        return Constants.DAMAGE;
    }
}
