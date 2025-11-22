package com.team12.redCorridor;

import java.io.IOException;

import com.team12.redCorridor.engine.EngineListener;
import com.team12.redCorridor.engine.GameEngine;
import com.team12.redCorridor.engine.GameFactory;
import com.team12.redCorridor.engine.GameSnapshot;
import com.team12.redCorridor.engine.GameState;
import com.team12.redCorridor.engine.InputHandler;
import com.team12.redCorridor.engine.World;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting RedCorridor...");

        GameFactory factory = new GameFactory();
        World world = factory.createDefaultWorld();
        InputHandler input = new InputHandler();
        EngineListener listener = new EngineListener() {
            @Override
            public void onFrame(GameSnapshot snapshot) {
                // placeholder CLI listener
            }

            @Override
            public void onStateChange(GameState state) {
                System.out.println("Engine state: " + state);
            }
        };
        GameEngine engine = new GameEngine(world, input, listener);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook: stopping engine...");
            engine.stop();
        }));
        // Start the game engine (runs on its own thread)
        engine.start(); // starts a simple game loop; expand engine as needed
        // When running from an IDE or terminal, let the user press ENTER to stop the
        // engine.
        System.out.println("Press ENTER to stop the engine.");
        try {
            System.in.read();
        } catch (IOException ignored) {
            // Ignore read errors; we'll still attempt to stop the engine
        }

        System.out.println("Stopping engine...");
        engine.stop();
    }
}
