package com.team12.redCorridor.entities;

import java.util.ArrayList;
import java.util.List;
import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.engine.*;

// This class represents the player character in the game
public class Player {

    // The player's current position on the map
    private Position position;

    // The player's current and maximum health
    private int health;
    private int maxHealth;

    // A list to store all collected key fragments
    private List<KeyFragment> fragments;

    // How many medkits the player currently has
    private static int medkitCount;

    // Constructor: sets up the player when the game starts
    public Player(Position startPosition) {
        this.position = startPosition;
        this.maxHealth = Constants.PLAYER_MAX_HEALTH; // set from Constants class
        this.health = maxHealth; // start with full health
        this.fragments = new ArrayList<>(); // empty list for fragments
        medkitCount = 0; // start with no medkits
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

    // Increases the player's health but not above maxHealth
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    // Reduces the player's health but not below zero
    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    // Adds a collected key fragment to the player's inventory
    public void addFragment(KeyFragment fragment) {
        fragments.add(fragment);
    }

    // Increases the number of medkits the player has
    public void addMedkit() {
        medkitCount++;
    }

    // Uses a medkit to heal the player (if available)
    public int useMedkit() {
        if (medkitCount > 0) {
            int before = health;
            heal(Constants.MEDKIT_HEAL_AMOUNT); // heal by a fixed amount
            medkitCount--; // reduce medkit count by 1
            return health - before;
        }
        return 0;
    }

    // --- Getters (to safely access private values) ---
    public Position getPosition() {
        return position;
    }

    public int getHealth() {
        return health;
    }

    public int getFragmentCount() {
        return fragments.size();
    }

    public int getMedkitCount() {
        return medkitCount;
    }
}
