package com.team12.redCorridor.entities;

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

// This class represents a collectible key fragment in the game
public class KeyFragment {

    // The position of the key fragment on the map
    private Position position;

    // Tells whether the key fragment has been collected or not
    private boolean collected;

    // Constructor: sets where the key fragment is and marks it as not collected yet
    public KeyFragment(Position position) {
        this.position = position;
        this.collected = false; // starts as not collected
    }

    // Marks this key fragment as collected
    public void collect() {
        collected = true;
    }

    // Returns true if the key fragment has already been collected
    public boolean isCollected() {
        return collected;
    }
}