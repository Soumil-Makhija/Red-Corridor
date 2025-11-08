package com.team12.redCorridor.engine;

import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.KeyFragment;
import com.team12.redCorridor.entities.Medkit;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;

public class GameEngine implements Runnable {
    private Thread thread;
    private volatile boolean running = false;

    private final GameFactory.World world;
    private final InputHandler input;
    private final EngineListener listener;

    private volatile GameState state = GameState.PLAYING;
    private static final long MOVE_INTERVAL_NS = 120_000_000L; // ~120ms between steps
    private long lastMoveTimeNs = 0L;
    private String lastMessage = "Use WASD/Arrow keys to move.";

    public GameEngine(GameFactory.World world, InputHandler input, EngineListener listener) {
        this.world = world;
        this.input = input;
        this.listener = listener;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "GameEngine-Thread");
        thread.start();
    }

    public void stop() {
        running = false;
        try { if (thread != null) thread.join(); } catch (InterruptedException ignored) {}
    }

    public GameState getState() { return state; }

    @Override
    public void run() {
        final int TARGET_FPS = 60;
        final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            if (now - lastTime >= NS_PER_FRAME) {
                update();
                lastTime = now;
            } else {
                try { Thread.sleep(1); } catch (InterruptedException ignored) {}
            }
        }
    }

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

    private void setState(GameState s) {
        if (state != s) {
            state = s;
            if (listener != null) listener.onStateChange(state);
        }
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
