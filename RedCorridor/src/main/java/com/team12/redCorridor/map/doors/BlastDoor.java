package com.team12.redCorridor.map.doors;

import com.team12.redCorridor.map.basicParts.Position;

public class BlastDoor extends Door {
    private int maxHealth;
    private int health;
    public BlastDoor(Position position, int maxHealth) {
        super(position, true, false);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public void takeDamage(int amount) {
        this.health -= amount;
    }


}
