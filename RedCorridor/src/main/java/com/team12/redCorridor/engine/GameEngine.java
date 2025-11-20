package com.team12.redCorridor.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.KeyFragment;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.entities.Trap;
import com.team12.redCorridor.utils.Constants;
import com.team12.redCorridor.ui.util.MapVisuals;

/**
 * Simple GameEngine that runs a fixed-timestep game loop on a background
 * thread.
 * - start(): launches the engine thread
 * - stop(): requests shutdown and waits for the thread to finish
 * - run(): main loop that calls update() and render() at the target FPS
 */
public class GameEngine implements Runnable {
    private static final long MOVE_INTERVAL_NS = 120_000_000L; // ~120 ms between steps

    private final World world;
    private final InputHandler input;
    private final EngineListener listener;

    // Thread used to run the game loop
    private Thread thread;
    // Volatile flag used to signal the loop to stop from other threads
    private volatile boolean running;
    private volatile GameState state = GameState.PLAYING;

    private long lastMoveTimeNs = 0L;
    private String lastMessage = "Use WASD/Arrow keys to move.";
    private final Random random = new Random();

    public GameEngine(World world, InputHandler input, EngineListener listener) {
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

    public World getWorld() {
        return world;
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

        String medkitMessage = null;
        if (input != null && input.consumeMedkitRequest()) {
            medkitMessage = useMedkitFromInventory();
        }

        // 4) Win/Lose checks
        GameState nextState = null;
        if (world.player.getHealth() <= 0) {
            nextState = GameState.GAME_OVER;
        } else if (hasCollectedAllFragments() && atExit()) {
            nextState = GameState.WIN;
        }

        // 5) Notify UI
        if (medkitMessage != null) {
            setMessage(medkitMessage);
        }
        GameFactory.refreshDynamicMap(world); // update new icons
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
        int prevX = player.getPosition().getX();
        int prevY = player.getPosition().getY();
        // new temp position
        int nx = prevX + dx;
        int ny = prevY + dy;

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
                switch (hiddenItem) {
                    case MapVisuals.FRAGMENT: {
                        world.baseMap[ny][nx] = hiddenItem;
                        collectFragmentAt(nx, ny);
                        interactionMessage = "You found a fragment.";
                        break;
                    }
                    case MapVisuals.MEDKIT: {
                        world.baseMap[ny][nx] = hiddenItem;
                        interactionMessage = collectMedkitAt(nx, ny);
                        break;
                    }
                    case MapVisuals.TRAP: {
                        world.baseMap[ny][nx] = hiddenItem;
                        triggerTrapAt(nx, ny);
                        interactionMessage = "A trap was triggered!";
                        break;
                    }
                    case MapVisuals.DRONE_TRIGGER: {
                        triggerDroneSpawnAt(nx, ny);
                        interactionMessage = "A drone has been activated!";
                        break;
                    }
                    default: {
                        world.baseMap[ny][nx] = MapVisuals.OPEN_DOOR;
                        interactionMessage = "Door opened.";
                        break;
                    }
                }
            } else {
                // Empty door
                world.baseMap[ny][nx] = MapVisuals.OPEN_DOOR;
                interactionMessage = "Door opened.";
            }
        } else {
            // Normal tile interaction
            interactionMessage = switch (tile) {
                case MapVisuals.FRAGMENT -> collectFragmentAt(nx, ny);
                case MapVisuals.MEDKIT -> collectMedkitAt(nx, ny);
                case MapVisuals.TRAP -> triggerTrapAt(nx, ny);
                case MapVisuals.DRONE_TRIGGER -> triggerDroneSpawnAt(nx, ny);
                default -> null;
            };
        }

        // display game message
        if (interactionMessage != null) {
            setMessage(interactionMessage);
        }
        return true;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < Constants.MAP_SIZE && y >= 0 && y < Constants.MAP_SIZE;
    }

    private boolean isWalkable(int x, int y) {
        return world.baseMap[y][x] != MapVisuals.WALL;
    }

    private String collectFragmentAt(int x, int y) {
        KeyFragment fragment = new KeyFragment(new Position(x, y));
        fragment.collect();
        world.player.addFragment(fragment);
        world.hiddenItems[y][x] = MapVisuals.EMPTY;
        world.revealedItems[y][x] = MapVisuals.FRAGMENT;
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Walkable after collecting
        return "Fragment collected.";
    }

    private String collectMedkitAt(int x, int y) {
        world.player.addMedkit();
        world.hiddenItems[y][x] = MapVisuals.EMPTY;
        world.revealedItems[y][x] = MapVisuals.MEDKIT;
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Walkable after collecting
        return "A medkit found. Press E to use.";
    }

    private String triggerTrapAt(int x, int y) {
        world.trapsTriggeredCount++;
        world.hiddenItems[y][x] = MapVisuals.EMPTY;
        world.revealedItems[y][x] = MapVisuals.TRAP;
        world.baseMap[y][x] = MapVisuals.OPEN_DOOR; // Walkable after triggering trap
        Trap trap = new Trap(new Position(x, y));
        world.player.takeDamage(trap.getDamage());
        return "Trap triggered! -" + trap.getDamage() + " HP.";
    }

    private String useMedkitFromInventory() {
        int healed = world.player.useMedkit();
        if (healed > 0) {
            return "Medkit used. +" + healed + " HP.";
        }
        return "No medkits available.";
    }

    private String triggerDroneSpawnAt(int doorX, int doorY) {
        world.baseMap[doorY][doorX] = MapVisuals.OPEN_DOOR;
        world.hiddenItems[doorY][doorX] = MapVisuals.EMPTY;
        world.revealedItems[doorY][doorX] = MapVisuals.EMPTY;
        Position spawn = findRandomSpawnPosition();
        if (spawn == null) {
            return "Empty room triggered, but no space for a new drone.";
        }
        world.drones.add(new Drone(spawn));
        return "Empty room triggered a new drone!";
    }

    private Position findRandomSpawnPosition() {
        List<Position> possibles = new ArrayList<>();
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (canSpawnDroneAt(x, y)) {
                    possibles.add(new Position(x, y));
                }
            }
        }
        if (possibles.isEmpty()) {
            return null;
        }
        return possibles.get(random.nextInt(possibles.size()));
    }

    private boolean canSpawnDroneAt(int x, int y) {
        if (!isWithinBounds(x, y)) {
            return false;
        }
        int tile = world.baseMap[y][x];
        if (tile == MapVisuals.WALL || tile == MapVisuals.EXIT || tile == MapVisuals.DOOR) {
            return false;
        }
        if (world.player.getPosition().getX() == x && world.player.getPosition().getY() == y) {
            return false;
        }
        for (Drone drone : world.drones) {
            if (drone.isActive()) {
                Position pos = drone.getPosition();
                if (pos.getX() == x && pos.getY() == y) {
                    return false;
                }
            }
        }
        return true;
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

        // deals multiple damage
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
        int[][] tiles = new int[Constants.MAP_SIZE][Constants.MAP_SIZE];
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            System.arraycopy(world.map[y], 0, tiles[y], 0, Constants.MAP_SIZE);
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
                world.trapsTriggeredCount,
                world.elapsedMillis(),
                lastMessage);
    }

    private void setMessage(String message) {
        if (message != null && !message.isBlank()) {
            lastMessage = message;
        }
    }
}
