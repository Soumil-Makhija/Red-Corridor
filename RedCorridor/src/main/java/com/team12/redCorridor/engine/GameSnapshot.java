package com.team12.redCorridor.engine;

/**
 * Lightweight, UI-friendly snapshot of the current frame.
 */
public record GameSnapshot(
        int[][] tiles,
        int playerX, int playerY,
        int hp, int medkits,
        int fragmentsCollected, int fragmentsRequired,
        int dronesTriggered, int trapsTriggered,
        long elapsedMillis,
        String message) {
}
