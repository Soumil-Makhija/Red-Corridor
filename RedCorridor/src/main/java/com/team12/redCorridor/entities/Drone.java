package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

/**
 * Enemy drone that moves toward the player.
 */
public class Drone {

    // The drone's current position on the map
    private Position position;

    // Use Damage value object for drone damage
    private Damage damage;

    // Whether the drone is still active (not destroyed)
    private boolean active;

    /**
     * Creates a new drone at the given position.
     *
     * @param startPosition starting position
     */
    public Drone(Position startPosition) {
        this.position = startPosition;
        this.damage = new Damage(Constants.DRONE_DAMAGE);
        this.active = true;
    }

    /**
     * Moves the drone one step closer to the target position.
     *
     * @param target position to move toward
     */
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

    /**
     * Deactivates the drone.
     */
    public void deactivate() {
        active = false;
    }

    // Getters (to read private values)
    /**
     * Returns true if the drone is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the drone's position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the damage value of the drone.
     */
    public int getDamage() {
        return damage.getValue();
    }
}
