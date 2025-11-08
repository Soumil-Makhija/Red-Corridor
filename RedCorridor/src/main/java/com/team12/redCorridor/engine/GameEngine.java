package com.team12.redCorridor.engine;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.KeyFragment;
import com.team12.redCorridor.entities.Medkit;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;

/**
 * Simple GameEngine that runs a fixed-timestep game loop on a background
 * thread.
 * - start(): launches the engine thread
 * - stop(): requests shutdown and waits for the thread to finish
 * - run(): main loop that calls update() and render() at the target FPS
 */
// Keep this class small and synchronous — move game logic into other engine
// classes

public class GameEngine implements Runnable {
    private static final long MOVE_INTERVAL_NS = 120_000_000L; // ~120 ms between steps

    private final GameFactory.World world;
    private final InputHandler input;
    private final EngineListener listener;

    // Thread used to run the game loop
    private Thread thread;
    // Volatile flag used to signal the loop to stop from other threads
    private volatile boolean running;
    private volatile GameState state = GameState.PLAYING;

    private long lastMoveTimeNs = 0L;
    private String lastMessage = "Use WASD/Arrow keys to move.";

    public GameEngine(GameFactory.World world, InputHandler input, EngineListener listener) {
        this.world = world;
        this.input = input;
        this.listener = listener;
    }

    /**
     * Start the engine. If already running this is a no-op.
     * Creates and starts the engine thread which executes run().
     */
    public void start() {
        if (running)
            return; // already running, ignore
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
            if (thread != null)
                thread.join();
        } catch (InterruptedException ignored) {
            // Interrupted while waiting for thread to finish — safe to ignore here
        }
    }

    /**
     * The engine thread entry point. Implements a fixed timestep loop targeting
     * TARGET_FPS frames per second. It uses System.nanoTime() for timing and
     * sleeps briefly when ahead of schedule to avoid busy-waiting.
     */
    public GameState getState() {
        return state;
    }

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
                lastTime = now;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
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
        if (state != GameState.PLAYING)
            return;

        long now = System.nanoTime();

        // 1) Read input
        int dx = 0, dy = 0;
        if (input != null) {
            if (input.isPressed(Direction.UP))
                dy = -1;
            if (input.isPressed(Direction.DOWN))
                dy = 1;
            if (input.isPressed(Direction.LEFT))
                dx = -1;
            if (input.isPressed(Direction.RIGHT))
                dx = 1;
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
        if (listener != null)
            listener.onFrame(buildSnapshot());

        // 6) Emit state change after latest snapshot has been delivered
        if (nextState != null) {
            setState(nextState);
        }
    }

    private void setState(GameState next) {
        if (state != next) {
            state = next;
            if (listener != null)
                listener.onStateChange(state);
        }
    }

    private boolean attemptMove(Player player, int dx, int dy) {
        int nx = player.getPosition().getX() + dx;
        int ny = player.getPosition().getY() + dy;

        if (!isWithinBounds(nx, ny)) {
            setMessage("Can't move outside the map.");
            return false;
        }
        if (!isWalkable(nx, ny)) {
            setMessage("Blocked by a wall.");
            return false;
        }

        int tile = world.baseMap[ny][nx];
        if (tile == MapVisuals.EXIT && !hasCollectedAllFragments()) {
            setMessage("Exit locked. Collect all fragments to unlock.");
            return false;
        }

        // Move
        player.getPosition().setX(nx);
        player.getPosition().setY(ny);

        // Interact with tile after moving
        String interactionMessage = null;

        // Check if this is a closed door - reveal and interact with hidden item
        if (tile == MapVisuals.DOOR) {
            int hiddenItem = world.hiddenItems[ny][nx];
            if (hiddenItem != MapVisuals.EMPTY) {
                // Reveal the actual item
                world.baseMap[ny][nx] = hiddenItem;
                interactionMessage = switch (hiddenItem) {
                    case MapVisuals.FRAGMENT -> collectFragmentAt(nx, ny);
                    case MapVisuals.MEDKIT -> applyMedkitAt(nx, ny);
                    case MapVisuals.DANGER -> triggerHazardAt(nx, ny);
                    default -> "Door opened.";
                };
            } else {
                // Empty door
                world.baseMap[ny][nx] = MapVisuals.OPEN_DOOR;
                interactionMessage = "Door opened.";
            }
        } else {
            // Normal tile interaction
            interactionMessage = switch (tile) {
                case MapVisuals.FRAGMENT -> collectFragmentAt(nx, ny);
                case MapVisuals.MEDKIT -> applyMedkitAt(nx, ny);
                case MapVisuals.DANGER -> triggerHazardAt(nx, ny);
                default -> null;
            };
        }

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
        KeyFragment fragment = new KeyFragment(new Position(x, y));
        fragment.collect();
        world.player.addFragment(fragment);
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Show open door after collecting
        return "Fragment collected.";
    }

    private String applyMedkitAt(int x, int y) {
        Medkit medkit = new Medkit(new Position(x, y));
        world.player.addMedkit();
        world.player.heal(medkit.getHealAmount());
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Show open door after collecting
        return "Medkit restored +" + medkit.getHealAmount() + " HP.";
    }

    private String triggerHazardAt(int x, int y) {
        world.hazardsTriggeredCount++;
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Show open door after triggering hazard
        world.player.takeDamage(80);
        return "Hazard triggered! -80 HP.";
    }

    private boolean hasCollectedAllFragments() {
        return world.totalFragmentsRequired <= 0
                || world.player.getFragmentCount() >= world.totalFragmentsRequired;
    }

    private boolean atExit() {
        int x = world.player.getPosition().getX();
        int y = world.player.getPosition().getY();
        return world.baseMap[y][x] == MapVisuals.EXIT;
    }

    private int moveDronesTowardPlayer() {
        if (world.drones.isEmpty())
            return 0;

        int totalDamage = 0;
        for (Drone drone : world.drones) {
            if (!drone.isActive())
                continue;

            Position dp = drone.getPosition();
            int ox = dp.getX();
            int oy = dp.getY();

            drone.moveToward(world.player.getPosition());

            int tx = dp.getX();
            int ty = dp.getY();
            if (!isWithinBounds(tx, ty) || !isWalkable(tx, ty)) {
                dp.setX(ox);
                dp.setY(oy);
            }

            if (tx == world.player.getPosition().getX() && ty == world.player.getPosition().getY()) {
                world.player.takeDamage(drone.getDamage());
                drone.deactivate();
                world.dronesTriggeredCount++;
                totalDamage += drone.getDamage();
            }
        }
        return totalDamage;
    }

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
                lastMessage);
    }

    private void setMessage(String message) {
        if (message != null && !message.isBlank()) {
            lastMessage = message;
        }
    }
}
