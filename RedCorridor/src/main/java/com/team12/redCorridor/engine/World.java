package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.List;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;
import com.team12.redCorridor.utils.Constants;

/**
 * Complete world state the engine mutates.
 */
public class World {
    public final int[][] baseMap = new int[Constants.MAP_SIZE][Constants.MAP_SIZE];
    public final int[][] map = new int[Constants.MAP_SIZE][Constants.MAP_SIZE];
    public final int[][] hiddenItems = new int[Constants.MAP_SIZE][Constants.MAP_SIZE]; // Track hidden items
    public final int[][] revealedItems = new int[Constants.MAP_SIZE][Constants.MAP_SIZE]; // Track what was found in
                                                                                          // rooms
    public final List<Drone> drones = new ArrayList<>();
    public Player player;

    public int totalFragmentsRequired;
    public int dronesTriggeredCount;
    public int trapsTriggeredCount;
    public long startTimeMillis;

    public int hp() {
        return player != null ? player.getHealth() : 0;
    }

    public int medkits() {
        return player != null ? player.getMedkitCount() : 0;
    }

    public int fragmentsCollected() {
        return player != null ? player.getKeyFragmentCount() : 0;
    }

    public long elapsedMillis() {
        return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : 0;
    }
        /**
     * Rebuilds the dynamic map from base tiles, revealed items, drones, and the player.
     * This used to live in GameFactory.refreshDynamicMap and was moved here
     * so the World manages its own visual state.
     */
    public void refreshDynamicMap() {
        // Copy base map into the dynamic map
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            System.arraycopy(baseMap[y], 0, map[y], 0, Constants.MAP_SIZE);
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (baseMap[y][x] == MapVisuals.OPEN_DOOR) {
                    int revealed = revealedItems[y][x];
                    if (revealed != MapVisuals.EMPTY) {
                        map[y][x] = revealed;
                    }
                }
            }
        }

        // Draw active drones
        for (Drone drone : drones) {
            if (drone.isActive()) {
                Position pos = drone.getPosition();
                map[pos.getY()][pos.getX()] = MapVisuals.DRONE;
            }
        }

        // Draw player
        if (player != null) {
            Position p = player.getPosition();
            map[p.getY()][p.getX()] = MapVisuals.PLAYER;
        }
    }
}