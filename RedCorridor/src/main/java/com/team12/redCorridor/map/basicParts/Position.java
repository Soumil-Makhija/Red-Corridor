package com.team12.redCorridor.map.basicParts;

/**
 * Stores a set of coordinates (X and Y) to store a position on the map.
 */

public class Position {
    private int xCoord;
    private int yCoord;

    public Position(int x, int y) {
        xCoord = x;
        yCoord = y;
    }
    public int getX() {
        return xCoord;
    }
    public int getY() {
        return yCoord;
    }
    public void setX(int x) {
        this.xCoord = x;
    }
    public void setY(int y) {
        this.yCoord = y;
    }
}
