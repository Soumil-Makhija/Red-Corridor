package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.List;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;

public class GameFactory {

    /** Complete world state the engine will mutate each tick. */
    public static class World {
        public static final int MAP_SIZE = 10;

        // Tiles (static layout) and dynamic view the engine refreshes
        public final int[][] baseMap = new int[MAP_SIZE][MAP_SIZE];
        public final int[][] map     = new int[MAP_SIZE][MAP_SIZE];

        public final List<Drone> drones = new ArrayList<>();
        public Player player;

        // Counters / rules
        public int totalFragmentsRequired;
        public int dronesTriggeredCount;
        public int hazardsTriggeredCount;

        // Time
        public long startTimeMillis;

        // Cached HUD
        public int hp() { return player.getHealth(); }
        public int medkits() { return player.getMedkitCount(); }
        public int fragmentsCollected() { return player.getFragmentCount(); }

        public long elapsedMillis() {
            return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : 0;
        }
    }

    /** Builds the same layout you had in GameSceneController.initializeBaseMap(). */
    public World createDefaultWorld() {
        World w = new World();

        // Bounds
        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (x == 0 || y == 0 || x == World.MAP_SIZE - 1 || y == World.MAP_SIZE - 1) {
                    w.baseMap[y][x] = MapVisuals.WALL;
                } else {
                    w.baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }

        // Walls (same pattern)
        w.baseMap[4][6] = MapVisuals.WALL;
        w.baseMap[4][5] = MapVisuals.WALL;
        w.baseMap[4][4] = MapVisuals.WALL;
        w.baseMap[5][4] = MapVisuals.WALL;
        w.baseMap[6][4] = MapVisuals.WALL;
        w.baseMap[6][5] = MapVisuals.WALL;
        w.baseMap[6][6] = MapVisuals.WALL;

        // Fragments
        w.baseMap[2][8] = MapVisuals.FRAGMENT;
        w.baseMap[1][5] = MapVisuals.FRAGMENT;
        w.baseMap[1][7] = MapVisuals.FRAGMENT;
        w.baseMap[2][7] = MapVisuals.FRAGMENT;
        w.baseMap[5][8] = MapVisuals.FRAGMENT;
        w.baseMap[7][6] = MapVisuals.FRAGMENT;

        // Medkits
        w.baseMap[2][1] = MapVisuals.MEDKIT;
        w.baseMap[2][3] = MapVisuals.MEDKIT;
        w.baseMap[7][3] = MapVisuals.MEDKIT;
        w.baseMap[7][2] = MapVisuals.MEDKIT;
        w.baseMap[4][2] = MapVisuals.MEDKIT;

        // Drones (will be spawned as entities)
        w.baseMap[3][3] = MapVisuals.DRONE;
        w.baseMap[3][4] = MapVisuals.DRONE;
        w.baseMap[3][5] = MapVisuals.DRONE;

        // Exit + Hazard
        w.baseMap[1][1] = MapVisuals.EXIT;
        w.baseMap[8][8] = MapVisuals.DANGER;

        // Player
        w.player = new Player(new Position(5, 5));
        w.startTimeMillis = System.currentTimeMillis();

        // Spawn drones and count fragments
        spawnDronesFromBaseMap(w);
        w.totalFragmentsRequired = countOnMap(w.baseMap, MapVisuals.FRAGMENT);

        refreshDynamicMap(w);
        return w;
    }

    private static void spawnDronesFromBaseMap(World w) {
        w.drones.clear();
        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (w.baseMap[y][x] == MapVisuals.DRONE) {
                    w.drones.add(new Drone(new Position(x, y)));
                    w.baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }
    }

    private static int countOnMap(int[][] map, int target) {
        int count = 0;
        for (int y = 0; y < World.MAP_SIZE; y++) {
            for (int x = 0; x < World.MAP_SIZE; x++) {
                if (map[y][x] == target) count++;
            }
        }
        return count;
    }

    public static void refreshDynamicMap(World w) {
        for (int y = 0; y < World.MAP_SIZE; y++) {
            System.arraycopy(w.baseMap[y], 0, w.map[y], 0, World.MAP_SIZE);
        }
        for (Drone d : w.drones) {
            if (d.isActive()) {
                Position p = d.getPosition();
                w.map[p.getY()][p.getX()] = MapVisuals.DRONE;
            }
        }
        w.map[w.player.getPosition().getY()][w.player.getPosition().getX()] = MapVisuals.PLAYER;
    }
}
