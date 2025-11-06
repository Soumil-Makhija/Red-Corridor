package com.team12.redCorridor.map.traps;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.map.basicParts.*;

public abstract class Trap extends Location {
    protected boolean isActive = true;
    public Trap(Position position) {
        super(position);
    }

    public abstract void debuffPlayer(Player player);
    public abstract void deactivate();

}
