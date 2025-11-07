package com.team12.redCorridor.engine;

import java.awt.Rectangle;
import java.util.List;

/**
 * Minimal collision helper utilities.
 * Keep collision math here so it is reusable by different engine systems.
 */
public class CollisionHandler {

    /**
     * Test whether two rectangles intersect.
     */
    public static boolean intersects(Rectangle a, Rectangle b) {
        return a.intersects(b);
    }

    /**
     * Check whether rectangle 'a' collides with any rectangle in the list.
     */
    public static boolean collidesWithAny(Rectangle a, List<Rectangle> list) {
        for (Rectangle r : list) {
            if (a.intersects(r)) return true;
        }
        return false;
    }
}