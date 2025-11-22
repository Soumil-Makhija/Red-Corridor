package com.team12.redCorridor.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.team12.redCorridor.utils.Constants;

public class MedkitTest {

    @Test
    public void medkitHealAmountComesFromConstants() {
        Medkit medkit = new Medkit(new Position(0, 0));
        assertEquals(Constants.MEDKIT_HEAL_AMOUNT, medkit.getHealAmount());
    }
}
