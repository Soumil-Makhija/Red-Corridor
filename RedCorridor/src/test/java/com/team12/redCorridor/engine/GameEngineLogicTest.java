package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;
import com.team12.redCorridor.utils.Constants;

public class GameEngineLogicTest {

    private World world;
    private InputHandler input;
    private GameEngine engine;

    @Before
    public void setUp() {
        world = new World();

        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                world.baseMap[y][x] = MapVisuals.EMPTY;
                world.hiddenItems[y][x] = MapVisuals.EMPTY;
                world.revealedItems[y][x] = MapVisuals.EMPTY;
            }
        }

        world.player = new Player(new Position(5, 5));
        world.totalFragmentsRequired = 1;

        input = new InputHandler();

        engine = new GameEngine(world, input, null);
    }

    @SuppressWarnings("unchecked")
    private boolean invokeAttemptMove(int dx, int dy) throws Exception {
        Method m = GameEngine.class.getDeclaredMethod("attemptMove",
                com.team12.redCorridor.entities.Player.class, int.class, int.class);
        m.setAccessible(true);
        return (boolean) m.invoke(engine, world.player, dx, dy);
    }

    @Test
    public void attemptMoveBlocksWalls() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        world.baseMap[startY][startX + 1] = MapVisuals.WALL;

        boolean moved = invokeAttemptMove(1, 0);

        assertFalse("Player should not move into a wall", moved);
        assertEquals(startX, world.player.getPosition().getX());
        assertEquals(startY, world.player.getPosition().getY());
    }

    @Test
    public void attemptMovePreventsExitUntilFragmentsCollected() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        world.baseMap[startY][startX + 1] = MapVisuals.EXIT;
        world.totalFragmentsRequired = 1; 

        boolean moved = invokeAttemptMove(1, 0);

        assertFalse("Player should not enter EXIT without fragments", moved);
        assertEquals(startX, world.player.getPosition().getX());
        assertEquals(startY, world.player.getPosition().getY());
    }

    @Test
    public void attemptMoveAllowsExitWhenFragmentsCollected() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        world.baseMap[startY][startX + 1] = MapVisuals.EXIT;
        world.totalFragmentsRequired = 0; 

        boolean moved = invokeAttemptMove(1, 0);

        assertTrue("Player should be able to enter EXIT when fragments collected", moved);
        assertEquals(startX + 1, world.player.getPosition().getX());
        assertEquals(startY, world.player.getPosition().getY());
    }

    @Test
    public void doorWithFragmentRevealsFragmentAndIncrementsCount() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        int targetX = startX + 1;
        int targetY = startY;

        world.baseMap[targetY][targetX] = MapVisuals.DOOR;
        world.hiddenItems[targetY][targetX] = MapVisuals.FRAGMENT;

        int beforeFragments = world.player.getFragmentCount();

        boolean moved = invokeAttemptMove(1, 0);
        assertTrue(moved);

        assertEquals(targetX, world.player.getPosition().getX());
        assertEquals(targetY, world.player.getPosition().getY());

        assertEquals(beforeFragments + 1, world.player.getFragmentCount());
        assertEquals(MapVisuals.EMPTY, world.hiddenItems[targetY][targetX]);
        assertEquals(MapVisuals.FRAGMENT, world.revealedItems[targetY][targetX]);
        assertEquals(MapVisuals.OPEN_DOOR, world.baseMap[targetY][targetX]);
    }

    @Test
    public void doorWithMedkitAddsMedkitAndReveals() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        int targetX = startX + 1;
        int targetY = startY;

        world.baseMap[targetY][targetX] = MapVisuals.DOOR;
        world.hiddenItems[targetY][targetX] = MapVisuals.MEDKIT;

        int beforeMedkits = world.player.getMedkitCount();

        boolean moved = invokeAttemptMove(1, 0);
        assertTrue(moved);

        assertEquals(beforeMedkits + 1, world.player.getMedkitCount());
        assertEquals(MapVisuals.EMPTY, world.hiddenItems[targetY][targetX]);
        assertEquals(MapVisuals.MEDKIT, world.revealedItems[targetY][targetX]);
        assertEquals(MapVisuals.OPEN_DOOR, world.baseMap[targetY][targetX]);
    }

    @Test
    public void doorWithTrapDamagesPlayerAndCountsTrap() throws Exception {
        int startX = world.player.getPosition().getX();
        int startY = world.player.getPosition().getY();

        int targetX = startX + 1;
        int targetY = startY;

        world.baseMap[targetY][targetX] = MapVisuals.DOOR;
        world.hiddenItems[targetY][targetX] = MapVisuals.TRAP;

        int beforeHp = world.player.getHealth();
        int beforeTraps = world.trapsTriggeredCount;

        boolean moved = invokeAttemptMove(1, 0);
        assertTrue(moved);

        assertEquals(beforeTraps + 1, world.trapsTriggeredCount);
        assertTrue("Player should take damage from trap",
                world.player.getHealth() < beforeHp);
        assertEquals(MapVisuals.EMPTY, world.hiddenItems[targetY][targetX]);
        assertEquals(MapVisuals.TRAP, world.revealedItems[targetY][targetX]);
        assertEquals(MapVisuals.OPEN_DOOR, world.baseMap[targetY][targetX]);
    }

    @Test
    public void runTest() throws Exception {


    }
}
