package com.team12.redCorridor.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class PositionTest {

    @Test
    public void constructorStoresCoordinates() {
        Position pos = new Position(3, 5);
        assertEquals(3, pos.getX());
        assertEquals(5, pos.getY());
    }

    @Test
    public void settersUpdateCoordinates() {
        Position pos = new Position(0, 0);
        pos.setX(7);
        pos.setY(9);
        assertEquals(7, pos.getX());
        assertEquals(9, pos.getY());
    }
}
