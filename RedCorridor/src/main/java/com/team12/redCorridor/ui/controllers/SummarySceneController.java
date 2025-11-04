package com.team12.redCorridor.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class SummarySceneController extends Controller {
    @FXML
    protected Pane mapPane;

    private Canvas canvas;
    private int[][] mapData;

    @FXML
    private void initialize() {
        if (mapPane == null) {
            return;
        }

        canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        mapPane.getChildren().add(canvas);
        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        drawMap();
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

    private void drawMap() {
        if (canvas == null) {
            return;
        }

        int[][] map = mapData != null ? mapData : defaultMap();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        int rows = map.length;
        int cols = map[0].length;
        double cellWidth = width / cols;
        double cellHeight = height / rows;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int value = map[y][x];
                switch (value) {
                    case 1 -> gc.setFill(Color.DARKGRAY);
                    case 2 -> gc.setFill(Color.CORNFLOWERBLUE);
                    default -> gc.setFill(Color.LIGHTGRAY);
                }
                gc.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    private int[][] defaultMap() {
        int size = 10;
        int[][] defaultMap = new int[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (y == 0 || y == size - 1 || x == 0 || x == size - 1) {
                    defaultMap[y][x] = 1;
                } else {
                    defaultMap[y][x] = 0;
                }
            }
        }
        defaultMap[size / 2][size / 2] = 2;
        return defaultMap;
    }
}
