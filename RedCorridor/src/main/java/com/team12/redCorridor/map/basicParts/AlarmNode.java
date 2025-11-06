package com.team12.redCorridor.map.basicParts;

import com.team12.redCorridor.entities.Drone;

public class AlarmNode extends Location {
    private boolean isActive = true;
    private Drone drone;

    public AlarmNode(Position position, Drone drone) {
        super(position);
        this.drone =  drone;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        isActive = false;
        drone.deactivate();
    }
}
