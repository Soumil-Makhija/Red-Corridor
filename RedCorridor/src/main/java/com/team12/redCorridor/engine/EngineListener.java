package com.team12.redCorridor.engine;

public interface EngineListener {
    void onFrame(GameSnapshot snapshot);
    void onStateChange(GameState state);
}
