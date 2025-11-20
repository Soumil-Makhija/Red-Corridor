package com.team12.redCorridor.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.team12.redCorridor.utils.Constants;

public class TrapTest {

    @Test
    public void trapStoresPositionAndReturnsDamageConstant() {
        Position pos = new Position(4, 7);
        Trap trap = new Trap(pos);

        assertSame(pos, trap.getPosition());
        assertEquals(Constants.DAMAGE, trap.getDamage());
    }
}
