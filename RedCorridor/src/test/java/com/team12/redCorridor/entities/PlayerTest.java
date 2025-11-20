package com.team12.redCorridor.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.engine.Direction;

public class PlayerTest {

    private Player createDefaultPlayerAt(int x, int y) {
        return new Player(new Position(x, y));
    }

    @Test
    public void constructorInitializesHealthAndPosition() {
        Player player = createDefaultPlayerAt(2, 3);
        assertEquals(Constants.PLAYER_MAX_HEALTH, player.getHealth());
        assertEquals(2, player.getPosition().getX());
        assertEquals(3, player.getPosition().getY());
        assertEquals(0, player.getFragmentCount());
        assertTrue(player.getMedkitCount() >= 0);
    }

    @Test
    public void moveChangesPositionCorrectly() {
        Player player = createDefaultPlayerAt(5, 5);

        player.move(Direction.UP);
        assertEquals(5, player.getPosition().getX());
        assertEquals(4, player.getPosition().getY());

        player.move(Direction.DOWN);
        assertEquals(5, player.getPosition().getX());
        assertEquals(5, player.getPosition().getY());

        player.move(Direction.LEFT);
        assertEquals(4, player.getPosition().getX());
        assertEquals(5, player.getPosition().getY());

        player.move(Direction.RIGHT);
        assertEquals(5, player.getPosition().getX());
        assertEquals(5, player.getPosition().getY());
    }

    @Test
    public void healDoesNotExceedMaxHealth() {
        Player player = createDefaultPlayerAt(0, 0);
        player.takeDamage(30);
        assertEquals(Constants.PLAYER_MAX_HEALTH - 30, player.getHealth());

        player.heal(1000);
        assertEquals(Constants.PLAYER_MAX_HEALTH, player.getHealth());
    }

    @Test
    public void takeDamageDoesNotGoBelowZero() {
        Player player = createDefaultPlayerAt(0, 0);
        player.takeDamage(Constants.PLAYER_MAX_HEALTH + 50);
        assertEquals(0, player.getHealth());
    }

    @Test
    public void addFragmentIncreasesFragmentCount() {
        Player player = createDefaultPlayerAt(0, 0);
        assertEquals(0, player.getFragmentCount());

        KeyFragment fragment1 = new KeyFragment(new Position(1, 1));
        fragment1.collect();
        player.addFragment(fragment1);

        assertEquals(1, player.getFragmentCount());

        KeyFragment fragment2 = new KeyFragment(new Position(2, 2));
        fragment2.collect();
        player.addFragment(fragment2);

        assertEquals(2, player.getFragmentCount());
    }

    @Test
    public void addMedkitAndUseMedkitAffectsHealthAndCount() {
        Player player = createDefaultPlayerAt(0, 0);

        int initialCount = player.getMedkitCount();

        player.takeDamage(40);
        int damagedHealth = player.getHealth();
        assertEquals(Constants.PLAYER_MAX_HEALTH - 40, damagedHealth);

        player.addMedkit();
        assertEquals(initialCount + 1, player.getMedkitCount());

        int healedAmount = player.useMedkit();
        assertTrue("Healed amount should be positive", healedAmount > 0);
        assertEquals(damagedHealth + healedAmount, player.getHealth());
        assertEquals("Medkit count should be back to initial", initialCount, player.getMedkitCount());
    }
}
