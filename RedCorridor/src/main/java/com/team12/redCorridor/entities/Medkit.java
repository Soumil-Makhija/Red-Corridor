package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

// This class represents a position or point on a 2D grid
class Position {
    // x and y store the coordinates
    private int x;
    private int y;

    // Constructor: runs when we create a new Position
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Returns the current X coordinate
    public int getX() {
        return x;
    }

    // Returns the current Y coordinate
    public int getY() {
        return y;
    }

    // Updates the X coordinate
    public void setX(int x) {
        this.x = x;
    }

    // Updates the Y coordinate
    public void setY(int y) {
        this.y = y;
    }
}

// This class represents a healing item called a Medkit
public class Medkit {

    // The position of the medkit on the map
    private Position position;

    // How much health the medkit restores when used
    private int healAmount;

    // Constructor: sets where the medkit is and how much it heals
    public Medkit(Position position) {
        this.position = position;

        // The healing amount is stored in the Constants class
        this.healAmount = Constants.MEDKIT_HEAL_AMOUNT;
    }

    // Returns how much health this medkit can restore
    public int getHealAmount() {
        return healAmount;
    }
}