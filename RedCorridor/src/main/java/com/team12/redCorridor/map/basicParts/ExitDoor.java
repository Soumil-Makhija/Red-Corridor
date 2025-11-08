package com.team12.redCorridor.map.basicParts;

import com.team12.redCorridor.entities.Position;


public class ExitDoor extends Location{
    private final int requiredKeys;
    protected boolean isOpen = false;

    public ExitDoor(Position position, int requiredKeys) {
        super(position);
        this.requiredKeys = requiredKeys;
    }

    public boolean openDoor(int numKeys){
        if(numKeys < requiredKeys){
            return false;
        }else{
            isOpen = true;
            return true;
        }
    }
}
