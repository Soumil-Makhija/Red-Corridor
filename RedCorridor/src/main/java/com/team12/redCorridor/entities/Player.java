package com.team12.redCorridor.entities;

import java.util.ArrayList;
import java.util.List;
import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.engine.*;

/**
 * Represents the player character in the game.
 * Handles position, health, key fragments, and medkit inventory.
 */
public class Player {

    /**
     * The player's current position on the map.
     */
    private Position position;

    /**
     * Encapsulates player's health and max health.
     */
    private Health health;

    /**
     * List of collected key fragments.
     */
    private List<KeyFragment> keyFragments;

    /**
     * Manages medkit collection and usage.
     */
    private MedkitInventory medkitInventory;

    /**
     * Constructs a new Player at the given starting position.
     * Initializes health, key fragments, and medkit inventory.
     *
     * @param startPosition the initial position of the player
     */
    public Player(Position startPosition) {
        this.position = startPosition;
        this.health = new Health(Constants.PLAYER_MAX_HEALTH);
        this.keyFragments = new ArrayList<>();
        this.medkitInventory = new MedkitInventory();
    }

    /**
     * Moves the player one step in the given direction.
     *
     * @param dir the direction to move (UP, DOWN, LEFT, RIGHT)
     */
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

    /**
     * Heals the player by the specified amount.
     *
     * @param amount the amount to heal
     */
    public void heal(int amount) {
        health.increase(amount);
    }

    /**
     * Reduces the player's health by the specified amount.
     *
     * @param amount the amount of damage to take
     */
    public void takeDamage(int amount) {
        health.decrease(amount);
    }

    /**
     * Adds a key fragment to the player's collection.
     *
     * @param fragment the key fragment to add
     */
    public void addKeyFragment(KeyFragment fragment) {
        keyFragments.add(fragment);
    }

    /**
     * Collects a medkit and adds it to the inventory.
     */
    public void collectMedkit() {
        medkitInventory.collectMedkit();
    }

    /**
     * Uses a medkit from inventory to heal the player.
     *
     * @return the amount healed, or 0 if no medkits available
     */
    public int consumeMedkit() {
        int healed = medkitInventory.consumeMedkit(health);
        return healed;
    }

    /**
     * Gets the player's current position.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the player's current health value.
     *
     * @return the health value
     */
    public int getHealth() {
        return health.getValue();
    }

    /**
     * Gets the number of key fragments collected.
     *
     * @return the count of key fragments
     */
    public int getKeyFragmentCount() {
        return keyFragments.size();
    }

    /**
     * Gets the number of medkits in inventory.
     *
     * @return the count of medkits
     */
    public int getMedkitCount() {
        return medkitInventory.getMedkitCount();
    }
}
