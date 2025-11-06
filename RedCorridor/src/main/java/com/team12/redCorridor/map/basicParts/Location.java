package com.team12.redCorridor.map.basicParts;

public abstract class Location {
    private Position position;

    public Location(Position position) {
        this.position = position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    public int getX() {
        return position.getX();
    }
    public int getY() {
        return position.getY();
    }
}
