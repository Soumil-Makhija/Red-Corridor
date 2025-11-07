package com.team12.redCorridor.engine;
/**
 * Simple GameEngine that runs a fixed-timestep game loop on a background thread.
 * - start(): launches the engine thread
 * - stop(): requests shutdown and waits for the thread to finish
 * - run(): main loop that calls update() and render() at the target FPS
 */
 //Keep this class small and synchronous — move game logic into other engine classes
 
public class GameEngine implements Runnable {
    // Thread used to run the game loop
    private Thread thread;

    // Volatile flag used to signal the loop to stop from other threads
    private volatile boolean running = false;

    /**
     * Start the engine. If already running this is a no-op.
     * Creates and starts the engine thread which executes run().
     */
    public void start() {
        if (running) return; // already running, ignore
        running = true;
        thread = new Thread(this, "GameEngine-Thread");
        thread.start();
    }

    /**
     * Stop the engine. Clears the running flag and joins the engine thread
     * so stop() only returns once the loop has exited.
     */
    public void stop() {
        running = false;
        try {
            if (thread != null) thread.join();
        } catch (InterruptedException ignored) {
            // Interrupted while waiting for thread to finish — safe to ignore here
        }
    }

    /**
     * The engine thread entry point. Implements a fixed timestep loop targeting
     * TARGET_FPS frames per second. It uses System.nanoTime() for timing and
     * sleeps briefly when ahead of schedule to avoid busy-waiting.
     */
    @Override
    public void run() {
        final int TARGET_FPS = 60; // desired frames per second
        final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS; // nanoseconds per frame
        long lastTime = System.nanoTime();

        // Loop until running becomes false (set by stop())
        while (running) {
            long now = System.nanoTime();
            if (now - lastTime >= NS_PER_FRAME) {
                // It's time for the next frame: update game state and render
                update();
                render();
                lastTime = now;
            } else {
                // Sleep a little to reduce CPU usage while waiting for next frame
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                    // If interrupted, loop will check running flag and continue/exit
                }
            }
        }
    }

    /**
     * Update game logic. This should advance game state, process input,
     * move entities, and perform collision checks. Currently a placeholder
     * that prints a tick to the console.
     */
    private void update() {
        // TODO: update game state, entities, input handling, collisions
        System.out.println("Game update tick");
    }

    /**
     * Render the current game state. In a full application this would notify
     * a view (Swing/JavaFX/other) or draw to a buffer. Left empty for now.
     */
    private void render() {
        // TODO: render / notify view
    }
}