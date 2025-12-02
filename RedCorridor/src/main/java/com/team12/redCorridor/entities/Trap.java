package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Represents a trap
 */
public class Trap {
    private final Position position;
    private final Damage damage;

    public Trap(Position position) {
        this.position = position;
        this.damage = new Damage(Constants.DAMAGE);
    }

    public Position getPosition() {
        return position;
    }

    public Damage getDamage() {
        return damage;
    }
}
