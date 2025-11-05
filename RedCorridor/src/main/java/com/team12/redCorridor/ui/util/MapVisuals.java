package com.team12.redCorridor.ui.util;

import java.net.URL;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class MapVisuals {
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int PLAYER = 2;
    public static final int DRONE = 3;
    public static final int MEDKIT = 4;
    public static final int EXIT = 5;
    public static final int DANGER = 6;
    public static final int FRAGMENT = 7;

    private static final String ICON_BASE = "/com/team12/redCorridor/ui/icons/";
    private static final Image WALL_IMAGE = loadImage("wall.png");
    private static final Image PLAYER_IMAGE = loadImage("user.png");
    private static final Image DRONE_IMAGE = loadImage("drone.png");
    private static final Image MEDKIT_IMAGE = loadImage("medkit.png");
    private static final Image EXIT_IMAGE = loadImage("door.png");
    private static final Image HAZARD_IMAGE = loadImage("hazard.png");
    private static final Image FRAGMENT_IMAGE = loadImage("fragment.png");

    private MapVisuals() {
    }

    public static void draw(GraphicsContext gc, int[][] tiles) {
        if (gc == null || tiles == null || tiles.length == 0 || tiles[0].length == 0) {
            return;
        }

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

                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(cellX, cellY, cellWidth, cellHeight);

                if (value == WALL && WALL_IMAGE != null) {
                    gc.drawImage(WALL_IMAGE, cellX, cellY, cellWidth, cellHeight);
                } else {
                    Image icon = iconFor(value);
                    if (icon != null) {
                        drawCentered(gc, icon, cellX, cellY, cellWidth, cellHeight, iconSize);
                    }
                }

                gc.setStroke(Color.BLACK);
                gc.strokeRect(cellX, cellY, cellWidth, cellHeight);
            }
        }
    }

    private static void drawCentered(GraphicsContext gc, Image image, double cellX, double cellY,
            double cellWidth, double cellHeight, double targetSize) {
        double width = targetSize;
        double height = targetSize;
        double x = cellX + (cellWidth - width) / 2.0;
        double y = cellY + (cellHeight - height) / 2.0;
        gc.drawImage(image, x, y, width, height);
    }

    private static Image iconFor(int value) {
        return switch (value) {
            case PLAYER -> PLAYER_IMAGE;
            case DRONE -> DRONE_IMAGE;
            case MEDKIT -> MEDKIT_IMAGE;
            case EXIT -> EXIT_IMAGE;
            case DANGER -> HAZARD_IMAGE;
            case FRAGMENT -> FRAGMENT_IMAGE;
            default -> null;
        };
    }

    private static Image loadImage(String name) {
        URL url = MapVisuals.class.getResource(ICON_BASE + name);
        if (url == null) {
            System.err.println("Missing icon: " + ICON_BASE + name);
            return null;
        }
        return new Image(url.toExternalForm(), true);
    }
}
