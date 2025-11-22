package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import org.junit.Test;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.ui.util.MapVisuals;

public class GameFactoryWorldIntegrationTest {

    @Test
    public void createDefaultWorldInitializesPlayerAndExit() {
        GameSettings.setMode(GameMode.MEDIUM);

        GameFactory factory = new GameFactory();

        World world = factory.createDefaultWorld();

        assertNotNull(world.player);
        assertNotNull(world.player.getPosition());

        assertEquals(Constants.MAP_SIZE, world.baseMap.length);
        assertEquals(Constants.MAP_SIZE, world.map.length);

        boolean foundExit = false;
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (world.baseMap[y][x] == MapVisuals.EXIT) {
                    foundExit = true;
                    break;
                }
            }
            if (foundExit) {
                break;
            }
        }

        assertTrue("World should contain at least one exit tile", foundExit);

        assertTrue(
                "Fragments required should be non-negative",
                world.totalFragmentsRequired >= 0
        );

        assertTrue(
                "Start time should be initialized",
                world.startTimeMillis > 0
        );
    }

    @Test
    public void refreshDynamicMapPlacesPlayerAndActiveDrones() {
        GameSettings.setMode(GameMode.EASY);

        GameFactory factory = new GameFactory();

        World world = factory.createDefaultWorld();

        GameFactory.refreshDynamicMap(world);

        int px = world.player.getPosition().getX();
        int py = world.player.getPosition().getY();
        assertEquals(MapVisuals.PLAYER, world.map[py][px]);

        for (Drone drone : world.drones) {
            if (drone.isActive()) {
                int dx = drone.getPosition().getX();
                int dy = drone.getPosition().getY();
                assertEquals(MapVisuals.DRONE, world.map[dy][dx]);
            }
        }
    }
}
