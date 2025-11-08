package com.team12.redCorridor.map.traps;

import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;

public class RadLeak extends Trap {
    private double speedReduction;
    public RadLeak(Position position) {
        super(position);
    }

    @Override
    public void debuffPlayer(Player player) {

    }

    @Override
    public void deactivate() {
        isActive = false;
    }
}
