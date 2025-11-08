package com.team12.redCorridor.map.lockers;

import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.map.basicParts.AlarmNode;

public class SecurityLocker extends Locker {
    private AlarmNode alarmNode;
    public SecurityLocker(Position position, AlarmNode alarmNode) {
        super(position);
        this.alarmNode = alarmNode;
    }

    @Override
    public void open() {
        if(!isOpen) {
            alarmNode.activate();
            isOpen = true;
            if(hasMedkit) {
                Player.addMedkit();
            }
        }
    }
}
