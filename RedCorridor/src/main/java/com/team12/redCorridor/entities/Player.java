package com.team12.redCorridor.entities;

import java.util.ArrayList;
import java.util.List;
import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.engine.*;

// This class represents the player character in the game
public class Player {

    // The player's current position on the map
    private Position position;

    // Refactor (S4, S9): Use Health value object to encapsulate health and
    // maxHealth
    private Health health;

    // S1: Bad Naming - Rename to keyFragments
    private List<KeyFragment> keyFragments;

    // S1: Bad Naming - Rename to medkits
    private int medkits;

    // Constructor: sets up the player when the game starts
    // Refactor (S4, S9): Accepts Position and creates Health with max from
    // Constants
    // S1: Bad Naming - keyFragments, medkits
    public Player(Position startPosition) {
        this.position = startPosition;
        this.health = new Health(Constants.PLAYER_MAX_HEALTH);
        this.keyFragments = new ArrayList<>();
        this.medkits = 0;
    }

    // Moves the player one step in the chosen direction
    public void move(Direction dir) {
        if (dir == null)
            return; // do nothing if direction not given

        // Move depending on the direction value
        switch (dir) {
            case UP -> position.setY(position.getY() - 1); // move up (y decreases)
            case DOWN -> position.setY(position.getY() + 1); // move down (y increases)
            case LEFT -> position.setX(position.getX() - 1); // move left (x decreases)
            case RIGHT -> position.setX(position.getX() + 1); // move right (x increases)
        }
    }

    // Refactor (S4, S9): Use Health methods for healing and damage
    public void heal(int amount) {
        health.increase(amount);
    }

    public void takeDamage(int amount) {
        health.decrease(amount);
    }

    // S1: Bad Naming - addKeyFragment
    public void addKeyFragment(KeyFragment fragment) {
        keyFragments.add(fragment);
    }

    // S1: Bad Naming - collectMedkit
    public void collectMedkit() {
        medkits++;
    }

    // S1: Bad Naming - consumeMedkit
    public int consumeMedkit() {
        if (medkits > 0) {
            int before = health.getValue();
            heal(Constants.MEDKIT_HEAL_AMOUNT);
            medkits--;
            return health.getValue() - before;
        }
        return 0;
    }

    // --- Getters (to safely access private values) ---
    public Position getPosition() {
        return position;
    }

    public int getHealth() {
        return health.getValue();
    }

    // S1: Bad Naming - getKeyFragmentCount
    public int getKeyFragmentCount() {
        return keyFragments.size();
    }

    public int getMedkitCount() {
        return medkits;
    }
}
