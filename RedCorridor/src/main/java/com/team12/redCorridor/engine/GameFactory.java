package com.team12.redCorridor.engine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * GameFactory is responsible for creating initial game worlds / maps and
 * populating them with simple entities (player, obstacles, etc).
 * Keep factory logic here so world creation is centralised and easy to change.
 */
public class GameFactory {

    /**
     * Lightweight container that represents a minimal world used by the engine.
     * - player: rectangle representing player position + size
     * - obstacles: simple rectangular collision geometry
     * - width/height: world bounds (for potential clamping)
     */
    public static class World {
        public Rectangle player;
        public final List<Rectangle> obstacles = new ArrayList<>();
        public int width;
        public int height;
    }

    /**
     * Create a small default world for development and testing.
     * Replace with map loading or procedural generation later.
     */
    public World createDefaultWorld() {
        World world = new World();
        world.width = 800;
        world.height = 600;

        // Player starts near the top-left
        world.player = new Rectangle(50, 50, 32, 32);

        // A few static rectangular obstacles for collision testing
        world.obstacles.add(new Rectangle(200, 50, 64, 64));
        world.obstacles.add(new Rectangle(400, 300, 128, 32));
        world.obstacles.add(new Rectangle(100, 250, 32, 128));

        return world;
    }
}