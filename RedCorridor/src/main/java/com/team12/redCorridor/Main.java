package com.team12.redCorridor;

import java.io.IOException;

import com.team12.redCorridor.engine.GameEngine;

public class Main 
{
  public static void main( String[] args )
    {
        System.out.println("Starting RedCorridor...");
        GameEngine engine = new GameEngine();

        // Ensure the engine is stopped cleanly if the JVM exits (e.g., Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook: stopping engine...");
            engine.stop();
        }));

        // Start the game engine (runs on its own thread)
        engine.start(); // starts a simple game loop; expand engine as needed

        // When running from an IDE or terminal, let the user press ENTER to stop the engine.
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