package com.team12.redCorridor.ui.controllers;

import com.team12.redCorridor.ui.util.MapVisuals;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import com.team12.redCorridor.utils.Constants;

/**
 * Abstract Controller for Summary Scenes
 */
public abstract class SummarySceneController extends Controller {
    @FXML
    protected Pane mapPane;
    @FXML
    private Label fragmentsStatLabel;
    @FXML
    private Label dronesStatLabel;
    @FXML
    private Label trapsStatLabel;
    @FXML
    private Label timeStatLabel;
    @FXML
    private Label healthStatLabel;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private int[][] mapData;
    private SummaryStats summaryStats;

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
        updateStatLabels();
    }

    public void setMapData(int[][] mapData) {
        this.mapData = mapData;
        drawMap();
    }

    public void setStats(SummaryStats stats) {
        this.summaryStats = stats;
        updateStatLabels();
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

    private void updateStatLabels() {
        if (summaryStats == null) {
            return;
        }

        if (fragmentsStatLabel != null) {
            String fragmentProgress = summaryStats.getFragmentsRequired() > 0
                    ? summaryStats.getFragmentsCollected() + "/" + summaryStats.getFragmentsRequired()
                    : String.valueOf(summaryStats.getFragmentsCollected());
            fragmentsStatLabel.setText("Fragments retrieved: " + fragmentProgress);
        }
        if (dronesStatLabel != null) {
            dronesStatLabel.setText("Drones triggered: " + summaryStats.getDronesTriggered());
        }
        if (trapsStatLabel != null) {
            trapsStatLabel.setText("Traps triggered: " + summaryStats.getTrapsTriggered());
        }
        if (timeStatLabel != null) {
            timeStatLabel.setText("Time taken: " + formatElapsedTime(summaryStats.getElapsedMillis()));
        }
        if (healthStatLabel != null) {
            healthStatLabel.setText("Health remaining: " + summaryStats.getHealthRemaining());
        }
    }

    private int[][] defaultMap() {
        int[][] defaultTiles = new int[Constants.MAP_SIZE][Constants.MAP_SIZE];
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (y == 0 || y == Constants.MAP_SIZE - 1 || x == 0 || x == Constants.MAP_SIZE - 1) {
                    defaultTiles[y][x] = MapVisuals.WALL;
                } else {
                    defaultTiles[y][x] = MapVisuals.EMPTY;
                }
            }
        }
        defaultTiles[Constants.MAP_SIZE / 2][Constants.MAP_SIZE / 2] = MapVisuals.PLAYER;
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

    private String formatElapsedTime(long elapsedMillis) {
        if (elapsedMillis <= 0) {
            return "--:--";
        }
        long totalSeconds = elapsedMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
