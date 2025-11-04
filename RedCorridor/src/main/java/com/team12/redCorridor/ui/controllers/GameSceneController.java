package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameSceneController extends Controller {
    @FXML
    private Pane mapPane;

    private Canvas canvas;
    private int[][] map = new int[10][10];
    private int playerX;
    private int playerY;

    @FXML
    public void initialize() {
        // populate sample map (simple border walls and a player)
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == 0 || y == 0 || x == 9 || y == 9)
                    map[y][x] = 1; // wall
                else
                    map[y][x] = 0; // empty
            }
        }
        playerX = 5;
        playerY = 5;
        map[playerY][playerX] = 2; // player position

        // create canvas and add to pane
        canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        mapPane.getChildren().add(canvas);

        // redraw when the pane is resized
        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

        // initial draw
        drawMap();
    }

    private void resizeCanvas() {
        double w = Math.max(mapPane.getWidth(), mapPane.getPrefWidth());
        double h = Math.max(mapPane.getHeight(), mapPane.getPrefHeight());
        canvas.setWidth(w);
        canvas.setHeight(h);
        drawMap();
    }

    private void drawMap() {
        if (canvas == null)
            return;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // clear
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);

        double cellW = w / 10.0;
        double cellH = h / 10.0;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                int v = map[y][x];
                switch (v) {
                    case 0 -> gc.setFill(Color.LIGHTGRAY);
                    case 1 -> gc.setFill(Color.DARKGRAY);
                    case 2 -> gc.setFill(Color.CORNFLOWERBLUE);
                    default -> gc.setFill(Color.PINK);
                }
                gc.fillRect(x * cellW, y * cellH, cellW, cellH);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * cellW, y * cellH, cellW, cellH);
            }
        }

        // temp winning and losing squares (hardcoded)
        gc.setFill(Color.GREEN);
        gc.fillRect(1 * cellW, 1 * cellH, cellW, cellH);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(1 * cellW, 1 * cellH, cellW, cellH);

        gc.setFill(Color.RED);
        gc.fillRect(8 * cellW, 8 * cellH, cellW, cellH);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(8 * cellW, 8 * cellH, cellW, cellH);
    }

    // Movement methods that update player position and redraw
    public void moveUp() {
        attemptMove(playerX, playerY - 1);
    }

    public void moveDown() {
        attemptMove(playerX, playerY + 1);
    }

    public void moveLeft() {
        attemptMove(playerX - 1, playerY);
    }

    public void moveRight() {
        attemptMove(playerX + 1, playerY);
    }

    private void attemptMove(int newX, int newY) {
        // bounds check
        if (newX < 0 || newX >= map[0].length || newY < 0 || newY >= map.length)
            return;
        // don't move into walls (value 1)
        if (map[newY][newX] == 1)
            return;
        // move player
        map[playerY][playerX] = 0;
        playerX = newX;
        playerY = newY;
        map[playerY][playerX] = 2;
        drawMap();
        evaluateGameState();
    }

    // simple sample goals so you can test end scenes without game logic wired yet
    private void evaluateGameState() {
        if (playerX == 1 && playerY == 1) {
            showSummaryScene("WinScene.fxml");
        } else if (playerX == map[0].length - 2 && playerY == map.length - 2) {
            showSummaryScene("DefeatScene.fxml");
        }
    }

    private void showSummaryScene(String resource) {
        Stage currentStage = stage != null ? stage : getPrimaryStage();
        if (currentStage == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/" + resource));
            Parent root = loader.load();
            SummarySceneController summaryController = loader.getController();
            Scene summaryScene = new Scene(root);

            summaryController.setStage(currentStage);
            summaryController.recordKey(summaryScene);
            summaryController.setMapData(copyMapData());

            currentStage.setScene(summaryScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] copyMapData() {
        int rows = map.length;
        int cols = map[0].length;
        int[][] copy = new int[rows][cols];
        for (int y = 0; y < rows; y++) {
            System.arraycopy(map[y], 0, copy[y], 0, cols);
        }
        return copy;
    }

    @Override
    public void recordKey(Scene scene) {
        // override to handle movement keys in-game, while preserving help/quit
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> moveUp();
                case S, DOWN -> moveDown();
                case A, LEFT -> moveLeft();
                case D, RIGHT -> moveRight();
                case H -> helpMenu();
                case Q -> quit();
                default -> {
                }
            }
        });
    }

}
