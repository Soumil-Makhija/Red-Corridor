package com.team12.redCorridor.entities;

import com.team12.redCorridor.utils.Constants;

// S6: Low Cohesion - Medkit logic extracted from Player
public class MedkitInventory {
    private int medkits;

    public MedkitInventory() {
        this.medkits = 0;
    }

    public void collectMedkit() {
        medkits++;
    }

    public int consumeMedkit(Health health) {
        if (medkits > 0) {
            int before = health.getValue();
            health.increase(Constants.MEDKIT_HEAL_AMOUNT);
            medkits--;
            return health.getValue() - before;
        }
        return 0;
    }

    public int getMedkitCount() {
        return medkits;
    }
}