package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Represents a trap
 */
public class Trap {
    private final Position position;
    private final Damage damage;

    /**
     * Creates a trap at the given position.
     *
     * @param position trap position
     */
    public Trap(Position position) {
        this.position = position;
        this.damage = new Damage(Constants.DAMAGE);
    }

    /**
     * Gets the trap's position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the trap's damage value.
     */
    public Damage getDamage() {
        return damage;
    }
}
