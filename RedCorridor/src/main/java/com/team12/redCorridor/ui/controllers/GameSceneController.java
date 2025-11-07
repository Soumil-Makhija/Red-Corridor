package com.team12.redCorridor.ui.controllers;

import com.team12.redCorridor.ui.util.MapVisuals;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameSceneController extends Controller {
    private static final int MAP_SIZE = 10;

    @FXML
    private Pane mapPane;

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private final int[][] baseMap = new int[MAP_SIZE][MAP_SIZE]; // static map
    private final int[][] map = new int[MAP_SIZE][MAP_SIZE]; // dynamic map (store current state)
    private int playerX;
    private int playerY;

    @FXML
    public void initialize() {
        initializeBaseMap(); // create new map (hard coded)

        // hard coded player starting pos
        playerX = 5;
        playerY = 5;
        map[playerY][playerX] = MapVisuals.PLAYER;

        // creating new canvas
        canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        mapPane.getChildren().add(canvas);

        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

        drawMap();
    }

    /*
     * Currently hard coded map with default objects
     * Implement random objects
     */
    private void initializeBaseMap() {
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                if (x == 0 || y == 0 || x == MAP_SIZE - 1 || y == MAP_SIZE - 1) {
                    baseMap[y][x] = MapVisuals.WALL;
                } else {
                    baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }

        baseMap[4][6] = MapVisuals.WALL;
        baseMap[4][5] = MapVisuals.WALL;
        baseMap[4][4] = MapVisuals.WALL;
        baseMap[5][4] = MapVisuals.WALL;
        baseMap[6][4] = MapVisuals.WALL;
        baseMap[6][5] = MapVisuals.WALL;
        baseMap[6][6] = MapVisuals.WALL;

        baseMap[3][3] = MapVisuals.DRONE;
        baseMap[7][5] = MapVisuals.MEDKIT;
        baseMap[1][1] = MapVisuals.EXIT;
        baseMap[2][8] = MapVisuals.FRAGMENT;
        baseMap[8][8] = MapVisuals.DANGER;

        // sync static objects to dynamic map
        for (int y = 0; y < MAP_SIZE; y++) {
            System.arraycopy(baseMap[y], 0, map[y], 0, MAP_SIZE);
        }
    }

    private void resizeCanvas() {
        double width = Math.max(mapPane.getWidth(), mapPane.getPrefWidth());
        double height = Math.max(mapPane.getHeight(), mapPane.getPrefHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);
        drawMap();
    }

    // displaying icons for objects
    private void drawMap() {
        if (graphicsContext == null) {
            return;
        }
        MapVisuals.draw(graphicsContext, map);
    }

    // movement methods that update player position and redraw
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
        // validate moves
        if (newX < 0 || newX >= MAP_SIZE || newY < 0 || newY >= MAP_SIZE) {
            return;
        }
        if (baseMap[newY][newX] == MapVisuals.WALL) {
            return;
        }

        // update pos
        map[playerY][playerX] = baseMap[playerY][playerX];
        playerX = newX;
        playerY = newY;
        map[playerY][playerX] = MapVisuals.PLAYER;

        drawMap(); // display new map
        evaluateGameState(); // check end game conditions
    }

    // check end game conditions
    private void evaluateGameState() {
        // currently hard coded for testing
        if (playerX == 1 && playerY == 1) {
            showSummaryScene("WinScene.fxml");
        } else if (playerX == MAP_SIZE - 2 && playerY == MAP_SIZE - 2) {
            showSummaryScene("DefeatScene.fxml");
        }
    }

    // End game scenes
    private void showSummaryScene(String resource) {
        Stage currentStage = stage != null ? stage : getPrimaryStage();
        if (currentStage == null) {
            return;
        }

        try {
            // get win/defeat scene
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

    // copy map to display in summary scene
    private int[][] copyMapData() {
        int[][] copy = new int[MAP_SIZE][MAP_SIZE];
        for (int y = 0; y < MAP_SIZE; y++) {
            System.arraycopy(map[y], 0, copy[y], 0, MAP_SIZE);
        }
        return copy;
    }

    // get key options from user
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> moveUp();
                case S, DOWN -> moveDown();
                case A, LEFT -> moveLeft();
                case D, RIGHT -> moveRight();
                case H -> helpMenu();
                case L -> lobby();
                default -> {
                }
            }
        });
    }
}
