package com.team12.redCorridor.map.doors;

import com.team12.redCorridor.map.basicParts.Location;
import com.team12.redCorridor.map.basicParts.Position;

public class Door extends Location {
    protected boolean isOpen;
    protected boolean isLocked;

    public Door(Position position, boolean isLocked, boolean isOpen) {
        super(position);
        this.isOpen = isOpen;
        this.isLocked = isLocked;
    }
    public void open() {
        if(!isLocked && !isOpen) {
            isOpen = true;
        }
    }
}
