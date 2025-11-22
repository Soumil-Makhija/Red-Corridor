package com.team12.redCorridor.entities;

import static org.junit.Assert.*;
import org.junit.Test;

import com.team12.redCorridor.utils.Constants;

public class DroneTest {

    @Test
    public void droneStartsActiveAtGivenPositionWithConfiguredDamage() {
        Position start = new Position(0, 0);
        Drone drone = new Drone(start);

        assertTrue(drone.isActive());
        assertSame(start, drone.getPosition());
        assertEquals(Constants.DRONE_DAMAGE, drone.getDamage());
    }

    @Test
    public void moveTowardMovesOneStepAlongDominantAxis() {
        Drone drone = new Drone(new Position(0, 0));

        // Target further in X: should move X by +1
        Position targetX = new Position(5, 0);
        drone.moveToward(targetX);
        assertEquals(1, drone.getPosition().getX());
        assertEquals(0, drone.getPosition().getY());

        // Now target purely in Y: should move Y by +1
        Position targetY = new Position(1, 4);
        drone.moveToward(targetY);
        assertEquals(1, drone.getPosition().getX());
        assertEquals(1, drone.getPosition().getY());
    }

    @Test
    public void deactivatedDroneDoesNotMove() {
        Drone drone = new Drone(new Position(2, 2));
        Position target = new Position(10, 10);

        drone.deactivate();
        drone.moveToward(target);

        assertEquals(2, drone.getPosition().getX());
        assertEquals(2, drone.getPosition().getY());
        assertFalse(drone.isActive());
    }
}
