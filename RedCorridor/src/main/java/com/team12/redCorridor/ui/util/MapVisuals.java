package com.team12.redCorridor.ui.util;

import java.net.URL;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class MapVisuals {
    // simple enum for objects
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int PLAYER = 2;
    public static final int DRONE = 3;
    public static final int MEDKIT = 4;
    public static final int EXIT = 5;
    public static final int TRAP = 6;
    public static final int FRAGMENT = 7;
    public static final int DOOR = 8;
    public static final int OPEN_DOOR = 9;
    public static final int DRONE_TRIGGER = 10;

    // icons path
    private static final String ICON_BASE = "/com/team12/redCorridor/ui/icons/";
    private static final Image WALL_IMAGE = loadImage("wall.png");
    private static final Image PLAYER_IMAGE = loadImage("user.png");
    private static final Image DRONE_IMAGE = loadImage("drone.png");
    private static final Image MEDKIT_IMAGE = loadImage("medkit.png");
    private static final Image EXIT_IMAGE = loadImage("exit.png");
    private static final Image CLOSED_DOOR_IMAGE = loadImage("close-door.png");
    private static final Image OPEN_DOOR_IMAGE = loadImage("open-door.png");
    private static final Image TRAP_IMAGE = loadImage("hazard.png");
    private static final Image FRAGMENT_IMAGE = loadImage("fragment.png");

    private MapVisuals() {
    }

    // display icons on map
    public static void draw(GraphicsContext gc, int[][] tiles) {
        if (gc == null || tiles == null || tiles.length == 0 || tiles[0].length == 0) {
            return;
        }

        // get canvas
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        int rows = tiles.length;
        int cols = tiles[0].length;
        double cellWidth = canvasWidth / cols;
        double cellHeight = canvasHeight / rows;
        double iconSize = Math.min(cellWidth, cellHeight) * 0.7;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int value = tiles[y][x];
                double cellX = x * cellWidth;
                double cellY = y * cellHeight;

                // fill tiles' background with light gray
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(cellX, cellY, cellWidth, cellHeight);

                // draw icons on tiles
                if (value == WALL && WALL_IMAGE != null) {
                    gc.drawImage(WALL_IMAGE, cellX, cellY, cellWidth, cellHeight);
                } else {
                    Image icon = iconFor(value); // get icon
                    if (icon != null) {
                        drawCentered(gc, icon, cellX, cellY, cellWidth, cellHeight, iconSize);
                    }
                }

                // make tiles' border visible
                gc.setStroke(Color.BLACK);
                gc.strokeRect(cellX, cellY, cellWidth, cellHeight);
            }
        }
    }

    // draw icons at tile's center
    private static void drawCentered(GraphicsContext gc, Image image, double cellX, double cellY,
            double cellWidth, double cellHeight, double targetSize) {
        double width = targetSize;
        double height = targetSize;
        double x = cellX + (cellWidth - width) / 2.0;
        double y = cellY + (cellHeight - height) / 2.0;
        gc.drawImage(image, x, y, width, height);
    }

    // get icons image path
    private static Image iconFor(int value) {
        return switch (value) {
            case PLAYER -> PLAYER_IMAGE;
            case DRONE -> DRONE_IMAGE;
            case MEDKIT -> MEDKIT_IMAGE;
            case EXIT -> EXIT_IMAGE;
            case DOOR -> CLOSED_DOOR_IMAGE;
            case OPEN_DOOR -> OPEN_DOOR_IMAGE;
            case TRAP -> TRAP_IMAGE;
            case FRAGMENT -> FRAGMENT_IMAGE;
            default -> null;
        };
    }

    // load image from path
    private static Image loadImage(String name) {
        URL url = MapVisuals.class.getResource(ICON_BASE + name);
        if (url == null) {
            System.err.println("Missing icon: " + ICON_BASE + name);
            return null;
        }
        return new Image(url.toExternalForm(), false);
    }

    // Public helper to get icon for UI display
    public static Image getIconForTileType(int tileType) {
        return iconFor(tileType);
    }

    // Public helper to get name for tile type
    public static String getNameForTileType(int tileType) {
        return switch (tileType) {
            case EMPTY -> "Empty";
            case WALL -> "Wall";
            case PLAYER -> "Player";
            case DRONE -> "Drone";
            case MEDKIT -> "Medkit";
            case EXIT -> "Exit";
            case DOOR -> "Door";
            case OPEN_DOOR -> "Open Door";
            case TRAP -> "Trap";
            case FRAGMENT -> "Key Fragment";
            case DRONE_TRIGGER -> "Drone Trigger";
            default -> "Unknown";
        };
    }
}
