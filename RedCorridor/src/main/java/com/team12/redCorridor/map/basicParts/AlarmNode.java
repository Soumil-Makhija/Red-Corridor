package com.team12.redCorridor.map.basicParts;

import com.team12.redCorridor.engine.GameFactory;
import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Position;

public class AlarmNode extends Location {
    private boolean isActive = true;
    private Drone drone;

    public AlarmNode(Position position) {
        super(position);
        drone = new Drone(position);
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
        drone.deactivate();
    }
}
