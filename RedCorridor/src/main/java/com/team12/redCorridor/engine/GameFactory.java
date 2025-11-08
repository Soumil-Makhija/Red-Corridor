package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;

/**
 * Builds the world by loading one of the authored mazes (15x15 with 13x13
 * playable interior) and scattering interactive objects across the walkable
 * tiles.
 */
public class GameFactory {
    private static final int MAP_SIZE = 15;
    private static final int FRAGMENT_COUNT = 6;
    private static final int MEDKIT_COUNT = 4;
    private static final int DRONE_COUNT = 4;

    private final MazeLoader mazeLoader = new MazeLoader();
    private final Random random = new Random();

    /**
     * Complete world state the engine mutates.
     */
    public static class World {
        public static final int MAP_SIZE = GameFactory.MAP_SIZE;

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

    public World createDefaultWorld() {
        World world = new World();

        char[][] layout = mazeLoader.loadRandomMaze();
        List<Position> openTiles = new ArrayList<>();
        List<Position> roomTiles = new ArrayList<>();
        applyLayout(world, layout, openTiles, roomTiles);

        configureWorld(world, openTiles, roomTiles);
        refreshDynamicMap(world);
        return world;
    }

    private void applyLayout(World world, char[][] layout, List<Position> openTiles, List<Position> roomTiles) {
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                boolean edge = (x == 0 || y == 0 || x == MAP_SIZE - 1 || y == MAP_SIZE - 1);
                boolean wall = edge;
                if (!edge) {
                    char value = layout[y][x];
                    wall = (value == '#');
                    if (!wall) {
                        Position pos = new Position(x, y);
                        openTiles.add(pos);
                        if (value == 'R') {
                            roomTiles.add(pos);
                        }
                    }
                }
                world.baseMap[y][x] = wall ? MapVisuals.WALL : MapVisuals.EMPTY;
            }
        }
    }

    private void configureWorld(World world, List<Position> openTiles, List<Position> roomTiles) {
        if (openTiles.isEmpty()) {
            throw new IllegalStateException("Maze does not contain any playable tiles.");
        }

        Position start = chooseStart(roomTiles, openTiles);
        world.player = new Player(new Position(start.getX(), start.getY()));
        removePosition(openTiles, start);
        removePosition(roomTiles, start);

        Position exit = chooseFarthest(openTiles, start);
        if (exit != null) {
            world.baseMap[exit.getY()][exit.getX()] = MapVisuals.EXIT;
            removePosition(openTiles, exit);
        }

        Position hazard = chooseFarthest(openTiles, start);
        if (hazard != null) {
            world.baseMap[hazard.getY()][hazard.getX()] = MapVisuals.DANGER;
            removePosition(openTiles, hazard);
        }

        List<Position> placementPool = new ArrayList<>(openTiles);
        Collections.shuffle(placementPool, random);

        int fragmentsPlaced = placeTiles(placementPool, world.baseMap, MapVisuals.FRAGMENT, FRAGMENT_COUNT);
        placeTiles(placementPool, world.baseMap, MapVisuals.MEDKIT, MEDKIT_COUNT);
        spawnDrones(world, placementPool, DRONE_COUNT);

        world.totalFragmentsRequired = fragmentsPlaced;
        world.startTimeMillis = System.currentTimeMillis();
    }

    private Position chooseStart(List<Position> roomTiles, List<Position> fallback) {
        if (!roomTiles.isEmpty()) {
            return copy(findClosestToCenter(roomTiles));
        }
        return copy(findClosestToCenter(fallback));
    }

    private Position chooseFarthest(List<Position> candidates, Position reference) {
        if (candidates.isEmpty() || reference == null) {
            return null;
        }
        Position farthest = candidates.get(0);
        int bestDistance = -1;
        for (Position pos : candidates) {
            int dist = distanceSquared(pos, reference);
            if (dist > bestDistance) {
                bestDistance = dist;
                farthest = pos;
            }
        }
        return copy(farthest);
    }

    private Position findClosestToCenter(List<Position> candidates) {
        int center = MAP_SIZE / 2;
        return Collections.min(candidates, Comparator.comparingInt(p -> distanceSquared(p, center, center)));
    }

    private Position copy(Position original) {
        return new Position(original.getX(), original.getY());
    }

    private void removePosition(List<Position> positions, Position target) {
        positions.removeIf(p -> p.getX() == target.getX() && p.getY() == target.getY());
    }

    private int placeTiles(List<Position> pool, int[][] baseMap, int tileValue, int desiredCount) {
        int placed = 0;
        for (int i = 0; i < pool.size() && placed < desiredCount; i++) {
            Position pos = pool.get(i);
            baseMap[pos.getY()][pos.getX()] = tileValue;
            placed++;
            pool.remove(i);
            i--;
        }
        return placed;
    }

    private void spawnDrones(World world, List<Position> pool, int desiredCount) {
        int spawned = 0;
        for (int i = 0; i < pool.size() && spawned < desiredCount; i++) {
            Position pos = pool.get(i);
            world.drones.add(new Drone(new Position(pos.getX(), pos.getY())));
            pool.remove(i);
            i--;
            spawned++;
        }
    }

    private int distanceSquared(Position pos, Position reference) {
        return distanceSquared(pos, reference.getX(), reference.getY());
    }

    private int distanceSquared(Position pos, int targetX, int targetY) {
        int dx = pos.getX() - targetX;
        int dy = pos.getY() - targetY;
        return dx * dx + dy * dy;
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
