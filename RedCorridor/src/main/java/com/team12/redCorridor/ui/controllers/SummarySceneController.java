package com.team12.redCorridor.ui.controllers;

import com.team12.redCorridor.ui.util.MapVisuals;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * Abstract Controller for Summary Scenes
 */
public abstract class SummarySceneController extends Controller {
    private static final int DEFAULT_MAP_SIZE = 10; // hard coded

    @FXML
    protected Pane mapPane;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private int[][] mapData;

    @FXML
    private void initialize() {
        if (mapPane == null) {
            return;
        }

        // creating new map
        canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        mapPane.getChildren().add(canvas);
        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        drawMap(); // display icons
    }

    public void setMapData(int[][] mapData) {
        this.mapData = mapData;
        drawMap();
    }

    private void resizeCanvas() {
        if (canvas == null || mapPane == null) {
            return;
        }

        double width = mapPane.getWidth() > 0 ? mapPane.getWidth() : mapPane.getPrefWidth();
        double height = mapPane.getHeight() > 0 ? mapPane.getHeight() : mapPane.getPrefHeight();
        canvas.setWidth(width);
        canvas.setHeight(height);
        drawMap();
    }

    // display icons on map
    private void drawMap() {
        if (graphicsContext == null) {
            return;
        }

        int[][] tiles = mapData != null ? mapData : defaultMap();
        MapVisuals.draw(graphicsContext, tiles);
    }

    private int[][] defaultMap() {
        int[][] defaultTiles = new int[DEFAULT_MAP_SIZE][DEFAULT_MAP_SIZE];
        for (int y = 0; y < DEFAULT_MAP_SIZE; y++) {
            for (int x = 0; x < DEFAULT_MAP_SIZE; x++) {
                if (y == 0 || y == DEFAULT_MAP_SIZE - 1 || x == 0 || x == DEFAULT_MAP_SIZE - 1) {
                    defaultTiles[y][x] = MapVisuals.WALL;
                } else {
                    defaultTiles[y][x] = MapVisuals.EMPTY;
                }
            }
        }
        defaultTiles[DEFAULT_MAP_SIZE / 2][DEFAULT_MAP_SIZE / 2] = MapVisuals.PLAYER;
        return defaultTiles;
    }

    // get key options from user
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case N -> newGame(event);
                case L -> lobby();
                case Q -> quit(event);
                default -> {
                }
            }
        });
    }
}
