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
import com.team12.redCorridor.utils.Constants;

/**
 * Builds the world by loading one of the authored mazes (15x15 with 13x13
 * playable interior) and scattering interactive objects across the walkable
 * tiles.
 */
public class GameFactory {
    private final MazeLoader mazeLoader = new MazeLoader();
    private final Random random = new Random();

    // main world
    public World createDefaultWorld() {
        World world = new World();

        char[][] layout = mazeLoader.loadRandomMaze();
        List<Position> openTiles = new ArrayList<>();
        applyLayout(world, layout, openTiles);

        configureWorld(world, openTiles);
        refreshDynamicMap(world);
        return world;
    }

    private void applyLayout(World world, char[][] layout, List<Position> openTiles) {
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                boolean edge = (x == 0 || y == 0 || x == Constants.MAP_SIZE - 1 || y == Constants.MAP_SIZE - 1);
                boolean wall = edge;
                if (!edge) {
                    char value = layout[y][x];
                    wall = (value == '#');
                    if (!wall) {
                        if (value == 'E') {
                            world.baseMap[y][x] = MapVisuals.EXIT;
                            continue;
                        }
                        Position pos = new Position(x, y);
                        openTiles.add(pos);
                    }
                }
                world.baseMap[y][x] = wall ? MapVisuals.WALL : MapVisuals.EMPTY;
            }
        }
    }

    private void configureWorld(World world, List<Position> openTiles) {
        if (openTiles.isEmpty()) {
            throw new IllegalStateException("Maze does not contain any playable tiles.");
        }

        GameMode mode = GameSettings.getCurrentMode();

        Position start = copy(findClosestToCenter(openTiles));
        world.player = new Player(new Position(start.getX(), start.getY()));
        removePosition(openTiles, start);

        // Remove any tiles already marked as exit
        openTiles.removeIf(pos -> world.baseMap[pos.getY()][pos.getX()] == MapVisuals.EXIT);

        List<Position> placementPool = new ArrayList<>(openTiles);
        Collections.shuffle(placementPool, random);

        int fragmentsPlaced = placeHiddenTiles(placementPool, world.baseMap, world.hiddenItems, MapVisuals.FRAGMENT,
                mode.fragments());
        placeHiddenTiles(placementPool, world.baseMap, world.hiddenItems, MapVisuals.MEDKIT, mode.medkits());
        placeHiddenTiles(placementPool, world.baseMap, world.hiddenItems, MapVisuals.TRAP, mode.traps());
        placeHiddenTiles(placementPool, world.baseMap, world.hiddenItems, MapVisuals.DRONE_TRIGGER,
                mode.droneRooms());
        placeHiddenTiles(placementPool, world.baseMap, world.hiddenItems, MapVisuals.EMPTY, mode.emptyRooms());
        spawnDrones(world, placementPool, mode.drones());

        world.totalFragmentsRequired = fragmentsPlaced;
        world.startTimeMillis = System.currentTimeMillis();
    }

    private Position findClosestToCenter(List<Position> candidates) {
        int center = Constants.MAP_SIZE / 2;
        return Collections.min(candidates, Comparator.comparingInt(p -> distanceSquared(p, center, center)));
    }

    private Position copy(Position original) {
        return new Position(original.getX(), original.getY());
    }

    private void removePosition(List<Position> positions, Position target) {
        positions.removeIf(p -> p.getX() == target.getX() && p.getY() == target.getY());
    }

    private int placeHiddenTiles(List<Position> pool, int[][] baseMap, int[][] hiddenItems, int itemType,
            int desiredCount) {
        int placed = 0;
        for (int i = 0; i < pool.size() && placed < desiredCount; i++) {
            Position pos = pool.get(i);
            hiddenItems[pos.getY()][pos.getX()] = itemType; // Store actual item
            baseMap[pos.getY()][pos.getX()] = MapVisuals.DOOR; // Show closed door
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

    private int distanceSquared(Position pos, int targetX, int targetY) {
        int dx = pos.getX() - targetX;
        int dy = pos.getY() - targetY;
        return dx * dx + dy * dy;
    }

    public static void refreshDynamicMap(World world) {
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            System.arraycopy(world.baseMap[y], 0, world.map[y], 0, Constants.MAP_SIZE);
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (world.baseMap[y][x] == MapVisuals.OPEN_DOOR) {
                    int revealed = world.revealedItems[y][x];
                    if (revealed != MapVisuals.EMPTY) {
                        world.map[y][x] = revealed;
                    }
                }
            }
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
