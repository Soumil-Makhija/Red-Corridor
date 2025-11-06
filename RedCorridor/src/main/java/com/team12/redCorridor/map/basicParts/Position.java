package com.team12.redCorridor.map.basicParts;

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
}
