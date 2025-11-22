package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

// This class represents an enemy drone that can move toward the player
public class Drone {

    // The drone's current position on the map
    private Position position;

    // How much damage the drone can cause
    private int damage;

    // Whether the drone is still active (not destroyed)
    private boolean active;

    // Constructor is called when we create a new Drone
    // It sets the starting position, damage, and marks it as active
    public Drone(Position startPosition) {
        // Set the drone's starting position
        this.position = startPosition;

        // Set the amount of damage it does (value from the Constants class)
        this.damage = Constants.DRONE_DAMAGE;

        // The drone starts as active by default
        this.active = true;
    }

    // This method makes the drone move one step closer to the target each time it’s
    // called
    public void moveToward(Position target) {
        // Stop if this object is not active or the target doesn't exist
        if (!active || target == null) {
            return;
        }

        // Find the difference in X and Y positions between the current position and the
        // target
        int xDifference = target.getX() - position.getX();
        int yDifference = target.getY() - position.getY();

        // Move one step closer to the target
        // If the target is farther away in the X direction, move in X
        if (Math.abs(xDifference) > Math.abs(yDifference)) {
            // Move right if xDifference is positive, left if negative
            position.setX(position.getX() + Integer.signum(xDifference));
        }
        // Otherwise, move one step in the Y direction
        else {
            // Move down if yDifference is positive, up if negative
            position.setY(position.getY() + Integer.signum(yDifference));
        }
    }

    // Turns off/deactivates the drone so it stops moving
    public void deactivate() {
        active = false;
    }

    // Getters (to read private values)
    public boolean isActive() {
        return active;
    } // Check if drone is still active

    public Position getPosition() {
        return position;
    } // Get current position

    public int getDamage() {
        return damage;
    } // Get how much damage it can deal
}
