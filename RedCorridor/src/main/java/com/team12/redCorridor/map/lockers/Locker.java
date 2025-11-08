package com.team12.redCorridor.map.lockers;

import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.map.basicParts.Location;
import com.team12.redCorridor.entities.Position;

import java.util.Random;

public class Locker extends Location {
    protected boolean isOpen = false;
    protected boolean hasMedkit = false;
    private Random random = new Random();

    public Locker(Position position) {
        super(position);
        hasMedkit = random.nextBoolean();


    }
    public void open() {
        if(!isOpen) {
            isOpen = true;
            if(hasMedkit) {
                Player.addMedkit();
            }
        }
    }
}
