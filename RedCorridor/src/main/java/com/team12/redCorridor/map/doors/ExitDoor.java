package com.team12.redCorridor.map.doors;

import com.team12.redCorridor.map.basicParts.Position;

public class ExitDoor extends Door {
    private final int requiredKeys;
    public ExitDoor(Position position, boolean isLocked, boolean isOpen, int requiredKeys) {
        super(position, false, true);
        this.requiredKeys = requiredKeys;
    }

    public boolean checkKey(int numKeys){
        if(numKeys < requiredKeys){
            return false;
        }else{
            isLocked = false;
            return true;
        }
    }
}
