package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.List;

import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.Player;

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
        return player != null ? player.getFragmentCount() : 0;
    }

    public long elapsedMillis() {
        return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : 0;
    }
}
