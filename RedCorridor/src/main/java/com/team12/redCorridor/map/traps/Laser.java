package com.team12.redCorridor.map.traps;

import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.map.basicParts.Position;

public class Laser extends Trap {
    private int damage;

    public Laser(Position position, int damage) {
        super(position);
        this.damage = damage;
    }

    @Override
    public void debuffPlayer(Player player) {
        if(isActive) {
            player.takeDamage(damage);
        }
    }

    @Override
    public void deactivate() {
        isActive = false;
    }
}
