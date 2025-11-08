package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.List;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;

/**
 * GameFactory is responsible for creating initial game worlds / maps and
 * populating them with simple entities (player, obstacles, etc).
 * Keep factory logic here so world creation is centralised and easy to change.
 */
public class GameFactory {

    /**
     * Lightweight container that represents a minimal world used by the engine.
     * - player: rectangle representing player position + size
     * - obstacles: simple rectangular collision geometry
     * - width/height: world bounds (for potential clamping)
     */
    public static class World {
        public static final int MAP_SIZE = 10;

        public final int[][] baseMap = new int[MAP_SIZE][MAP_SIZE];
        public final int[][] map = new int[MAP_SIZE][MAP_SIZE];
        public final List<Drone> drones = new ArrayList<>();
        public Player player;

        public int totalFragmentsRequired;
        public int dronesTriggeredCount;
        public int hazardsTriggeredCount;
        public long startTimeMillis;

        public int hp() {
            return player != null ? player.getHealth() : 0;
        }

        public int medkits() {
            return player != null ? player.getMedkitCount() : 0;
        }

        public int fragmentsCollected() {
            return player != null ? player.getFragmentCount() : 0;
        }

        public long elapsedMillis() {
            return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : 0;
        }
    }

    /**
     * Create a small default world for development and testing.
     * Replace with map loading or procedural generation later.
     */
    public World createDefaultWorld() {
        World world = new World();

        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (x == 0 || y == 0 || x == World.MAP_SIZE - 1 || y == World.MAP_SIZE - 1) {
                    world.baseMap[y][x] = MapVisuals.WALL;
                } else {
                    world.baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }

        world.baseMap[4][6] = MapVisuals.WALL;
        world.baseMap[4][5] = MapVisuals.WALL;
        world.baseMap[4][4] = MapVisuals.WALL;
        world.baseMap[5][4] = MapVisuals.WALL;
        world.baseMap[6][4] = MapVisuals.WALL;
        world.baseMap[6][5] = MapVisuals.WALL;
        world.baseMap[6][6] = MapVisuals.WALL;

        world.baseMap[2][8] = MapVisuals.FRAGMENT;
        world.baseMap[1][5] = MapVisuals.FRAGMENT;
        world.baseMap[1][7] = MapVisuals.FRAGMENT;
        world.baseMap[2][7] = MapVisuals.FRAGMENT;
        world.baseMap[5][8] = MapVisuals.FRAGMENT;
        world.baseMap[7][6] = MapVisuals.FRAGMENT;

        world.baseMap[2][1] = MapVisuals.MEDKIT;
        world.baseMap[2][3] = MapVisuals.MEDKIT;
        world.baseMap[7][3] = MapVisuals.MEDKIT;
        world.baseMap[7][2] = MapVisuals.MEDKIT;
        world.baseMap[4][2] = MapVisuals.MEDKIT;

        world.baseMap[3][3] = MapVisuals.DRONE;
        world.baseMap[3][4] = MapVisuals.DRONE;
        world.baseMap[3][5] = MapVisuals.DRONE;

        world.baseMap[1][1] = MapVisuals.EXIT;
        world.baseMap[8][8] = MapVisuals.DANGER;

        world.player = new Player(new Position(5, 5));
        world.startTimeMillis = System.currentTimeMillis();

        spawnDronesFromBaseMap(world);
        world.totalFragmentsRequired = countOnMap(world.baseMap, MapVisuals.FRAGMENT);

        refreshDynamicMap(world);
        return world;
    }

    private static void spawnDronesFromBaseMap(World world) {
        world.drones.clear();
        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (world.baseMap[y][x] == MapVisuals.DRONE) {
                    world.drones.add(new Drone(new Position(x, y)));
                    world.baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }
    }

    private static int countOnMap(int[][] map, int target) {
        int count = 0;
        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (map[y][x] == target)
                    count++;
            }
        }
        return count;
    }

    public static void refreshDynamicMap(World world) {
        for (int y = 0; y < World.MAP_SIZE; y++) {
            System.arraycopy(world.baseMap[y], 0, world.map[y], 0, World.MAP_SIZE);
        }

        for (Drone drone : world.drones) {
            if (drone.isActive()) {
                Position pos = drone.getPosition();
                world.map[pos.getY()][pos.getX()] = MapVisuals.DRONE;
            }
        }

        if (world.player != null) {
            world.map[world.player.getPosition().getY()][world.player.getPosition().getX()] = MapVisuals.PLAYER;
        }
    }
}
