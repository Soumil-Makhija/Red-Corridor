package com.team12.redCorridor.entities;

/**
 * Represents a coordinate on the game grid.
 */
public class Position {
    private int x;
    private int y;

    /**
     * Creates a position with x and y.
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x coordinate.
     * 
     * @param x x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate.
     * 
     * @param y y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }
}
