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

        // 1) Read input
        int dx = 0, dy = 0;
        if (input != null) {
            if (input.isPressed(Direction.UP))    dy = -1;
            if (input.isPressed(Direction.DOWN))  dy =  1;
            if (input.isPressed(Direction.LEFT))  dx = -1;
            if (input.isPressed(Direction.RIGHT)) dx =  1;
        }

        // 2) Move player (at most one tile per frame)
        if (dx != 0 || dy != 0) attemptMove(world.player, dx, dy);

        // 3) Move drones + apply damage
        int dmg = moveDronesTowardPlayer();
        if (dmg > 0) {
            world.player.takeDamage(dmg);
            world.dronesTriggeredCount++;
        }

        // 4) Win/Lose checks
        if (world.player.getHealth() <= 0) {
            setState(GameState.GAME_OVER);
        } else if (hasCollectedAllFragments() && atExit()) {
            setState(GameState.WIN);
        }

        // 5) Notify UI
        GameFactory.refreshDynamicMap(world);
        if (listener != null) listener.onFrame(buildSnapshot());
    }

    private void setState(GameState s) {
        if (state != s) {
            state = s;
            if (listener != null) listener.onStateChange(state);
        }
    }

    // -------------------- World logic (migrated from controller) --------------------

    private void attemptMove(Player p, int dx, int dy) {
        int nx = p.getPosition().getX() + dx;
        int ny = p.getPosition().getY() + dy;
        if (!isWithinBounds(nx, ny) || !isWalkable(nx, ny)) return;

        int tile = world.baseMap[ny][nx];

        if (tile == MapVisuals.WALL) return;
        if (tile == MapVisuals.EXIT && !hasCollectedAllFragments()) {
            // locked, do not move
            return;
        }

        // Move
        p.getPosition().setX(nx);
        p.getPosition().setY(ny);

        // Interact with tile after moving
        switch (tile) {
            case MapVisuals.FRAGMENT -> collectFragmentAt(nx, ny);
            case MapVisuals.MEDKIT   -> applyMedkitAt(nx, ny);
            case MapVisuals.DANGER   -> triggerHazardAt(nx, ny);
            default -> { /* empty */ }
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < GameFactory.World.MAP_SIZE && y >= 0 && y < GameFactory.World.MAP_SIZE;
    }

    private boolean isWalkable(int x, int y) {
        return world.baseMap[y][x] != MapVisuals.WALL;
    }

    private void collectFragmentAt(int x, int y) {
        KeyFragment f = new KeyFragment(new Position(x, y));
        f.collect();
        world.player.addFragment(f);
        world.baseMap[y][x] = MapVisuals.EMPTY;
    }

    private void applyMedkitAt(int x, int y) {
        Medkit m = new Medkit(new Position(x, y));
        world.player.addMedkit();
        world.player.heal(m.getHealAmount());
        world.baseMap[y][x] = MapVisuals.EMPTY;
    }

    private void triggerHazardAt(int x, int y) {
        world.hazardsTriggeredCount++;
        world.baseMap[y][x] = MapVisuals.EMPTY;
        world.player.takeDamage(80); // same as HAZARD_DAMAGE in controller
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
            world.elapsedMillis()
        );
    }
}
