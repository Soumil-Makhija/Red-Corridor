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
        if (state != GameState.PLAYING) return;

        long now = System.nanoTime();

        // 1) Read input
        int dx = 0, dy = 0;
        if (input != null) {
            if (input.isPressed(Direction.UP))    dy = -1;
            if (input.isPressed(Direction.DOWN))  dy =  1;
            if (input.isPressed(Direction.LEFT))  dx = -1;
            if (input.isPressed(Direction.RIGHT)) dx =  1;
        }

        boolean playerMoved = false;
        // 2) Move player (at most one tile per frame)
        if ((dx != 0 || dy != 0) && now - lastMoveTimeNs >= MOVE_INTERVAL_NS) {
            if (attemptMove(world.player, dx, dy)) {
                lastMoveTimeNs = now;
                playerMoved = true;
            }
        }

        // 3) Move drones + apply damage (only after player moved)
        if (playerMoved) {
            int dmg = moveDronesTowardPlayer();
            if (dmg > 0) {
                setMessage("Drone caught you! -" + dmg + " HP");
            }
        }

        // 4) Win/Lose checks
        GameState nextState = null;
        if (world.player.getHealth() <= 0) {
            nextState = GameState.GAME_OVER;
        } else if (hasCollectedAllFragments() && atExit()) {
            nextState = GameState.WIN;
        }

        // 5) Notify UI
        GameFactory.refreshDynamicMap(world);
        if (listener != null) listener.onFrame(buildSnapshot());

        // 6) Emit state change after latest snapshot has been delivered
        if (nextState != null) {
            setState(nextState);
        }
    }

    /**
     * Render the current game state. In a full application this would notify
     * a view (Swing/JavaFX/other) or draw to a buffer. Left empty for now.
     */
    private void render() {
        // TODO: render / notify view
    }

    // -------------------- World logic (migrated from controller) --------------------

    private boolean attemptMove(Player p, int dx, int dy) {
        int nx = p.getPosition().getX() + dx;
        int ny = p.getPosition().getY() + dy;
        if (!isWithinBounds(nx, ny)) {
            setMessage("Can't move outside the map.");
            return false;
        }
        if (!isWalkable(nx, ny)) {
            setMessage("Blocked by a wall.");
            return false;
        }

        int tile = world.baseMap[ny][nx];

        if (tile == MapVisuals.WALL) return false;
        if (tile == MapVisuals.EXIT && !hasCollectedAllFragments()) {
            // locked, do not move
            setMessage("Exit locked. Collect all fragments to unlock.");
            return false;
        }

        // Move
        p.getPosition().setX(nx);
        p.getPosition().setY(ny);

        // Interact with tile after moving
        String interactionMessage = switch (tile) {
            case MapVisuals.FRAGMENT -> collectFragmentAt(nx, ny);
            case MapVisuals.MEDKIT   -> applyMedkitAt(nx, ny);
            case MapVisuals.DANGER   -> triggerHazardAt(nx, ny);
            default -> null;
        };
        if (interactionMessage != null) {
            setMessage(interactionMessage);
        }
        return true;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < GameFactory.World.MAP_SIZE && y >= 0 && y < GameFactory.World.MAP_SIZE;
    }

    private boolean isWalkable(int x, int y) {
        return world.baseMap[y][x] != MapVisuals.WALL;
    }

    private String collectFragmentAt(int x, int y) {
        KeyFragment f = new KeyFragment(new Position(x, y));
        f.collect();
        world.player.addFragment(f);
        world.baseMap[y][x] = MapVisuals.EMPTY;
        return "Fragment collected.";
    }

    private String applyMedkitAt(int x, int y) {
        Medkit m = new Medkit(new Position(x, y));
        world.player.addMedkit();
        world.player.heal(m.getHealAmount());
        world.baseMap[y][x] = MapVisuals.EMPTY;
        return "Medkit restored +" + m.getHealAmount() + " HP.";
    }

    private String triggerHazardAt(int x, int y) {
        world.hazardsTriggeredCount++;
        world.baseMap[y][x] = MapVisuals.EMPTY;
        world.player.takeDamage(80); // same as HAZARD_DAMAGE in controller
        return "Hazard triggered! -80 HP.";
    }

    private boolean hasCollectedAllFragments() {
        return world.totalFragmentsRequired <= 0
                || world.player.getFragmentCount() >= world.totalFragmentsRequired;
    }

    private boolean atExit() {
        return world.player.getPosition().getX() == 1 && world.player.getPosition().getY() == 1;
    }

    private int moveDronesTowardPlayer() {
        if (world.drones.isEmpty()) return 0;

        int totalDamage = 0;
        for (Drone d : world.drones) {
            if (!d.isActive()) continue;

            Position dp = d.getPosition();
            int ox = dp.getX(), oy = dp.getY();

            d.moveToward(world.player.getPosition());

            int tx = dp.getX(), ty = dp.getY();
            if (!isWithinBounds(tx, ty) || !isWalkable(tx, ty)) {
                dp.setX(ox); dp.setY(oy); // revert
            }

            if (tx == world.player.getPosition().getX() && ty == world.player.getPosition().getY()) {
                world.player.takeDamage(d.getDamage());
                d.deactivate();
                world.dronesTriggeredCount++;
                totalDamage += d.getDamage();
            }
        }
        return totalDamage;
    }

    //Snapshot

    private GameSnapshot buildSnapshot() {
        // tiles
        int[][] tiles = new int[GameFactory.World.MAP_SIZE][GameFactory.World.MAP_SIZE];
        for (int y = 0; y < GameFactory.World.MAP_SIZE; y++) {
            System.arraycopy(world.map[y], 0, tiles[y], 0, GameFactory.World.MAP_SIZE);
        }

        return new GameSnapshot(
            tiles,
            world.player.getPosition().getX(),
            world.player.getPosition().getY(),
            world.hp(),
            world.medkits(),
            world.fragmentsCollected(),
            world.totalFragmentsRequired,
            world.dronesTriggeredCount,
            world.hazardsTriggeredCount,
            world.elapsedMillis(),
            lastMessage
        );
    }

    private void setMessage(String message) {
        if (message != null && !message.isBlank()) {
            lastMessage = message;
        }
    }
}
