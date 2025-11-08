package com.team12.redCorridor.engine;

/**
 * Lightweight, UI-friendly snapshot of the current frame.
 * For now it's a stub so the project compiles; you'll fill real values later.
 */
public record GameSnapshot(
    int[][] tiles,
    int playerX, int playerY,
    int hp, int medkits,
    int fragmentsCollected, int fragmentsRequired,
    int dronesTriggered, int hazardsTriggered,
    long elapsedMillis
) {}