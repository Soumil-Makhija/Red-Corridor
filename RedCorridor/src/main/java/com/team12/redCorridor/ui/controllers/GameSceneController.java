package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import com.team12.redCorridor.engine.Direction;
import com.team12.redCorridor.engine.EngineListener;
import com.team12.redCorridor.engine.GameEngine;
import com.team12.redCorridor.engine.GameFactory;
import com.team12.redCorridor.engine.GameSnapshot;
import com.team12.redCorridor.engine.GameState;
import com.team12.redCorridor.engine.InputHandler;
import com.team12.redCorridor.engine.World;
import com.team12.redCorridor.ui.util.MapVisuals;
import com.team12.redCorridor.utils.Constants;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for Game Scene
 */
public class GameSceneController extends Controller {

    @FXML
    private Pane mapPane;
    @FXML
    private Label hpLabel;
    @FXML
    private Label medkitLabel;
    @FXML
    private Label fragmentsLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label roomNameLabel;
    @FXML
    private Label messageLabel;
    private GameEngine engine;
    private InputHandler input;
    private GameSnapshot lastSnapshot;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    // get key options from user
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> {
                    if (input != null)
                        input.press(Direction.UP);
                }
                case S, DOWN -> {
                    if (input != null)
                        input.press(Direction.DOWN);
                }
                case A, LEFT -> {
                    if (input != null)
                        input.press(Direction.LEFT);
                }
                case D, RIGHT -> {
                    if (input != null)
                        input.press(Direction.RIGHT);
                }
                case H -> helpMenu();
                case L -> lobby();
                default -> {
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W, UP -> {
                    if (input != null)
                        input.release(Direction.UP);
                }
                case S, DOWN -> {
                    if (input != null)
                        input.release(Direction.DOWN);
                }
                case A, LEFT -> {
                    if (input != null)
                        input.release(Direction.LEFT);
                }
                case D, RIGHT -> {
                    if (input != null)
                        input.release(Direction.RIGHT);
                }
                case E -> {
                    if (input != null)
                        input.requestMedkitUse();
                }
                default -> {
                }
            }
        });
    }

    // init game scene
    @FXML
    public void initialize() {
        setupCanvas();
        startEngine();
    }

    private void setupCanvas() {
        if (mapPane == null) {
            return;
        }

        canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        mapPane.getChildren().add(canvas);
        mapPane.widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        mapPane.heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
    }

    private void startEngine() {
        World world = new GameFactory().createDefaultWorld();
        input = new InputHandler();
        engine = new GameEngine(world, input, new EngineListener() {
            @Override
            public void onFrame(GameSnapshot snapshot) {
                javafx.application.Platform.runLater(() -> renderSnapshot(snapshot));
            }

            @Override
            public void onStateChange(GameState state) {
                javafx.application.Platform.runLater(() -> handleStateChange(state));
            }
        });
        engine.start();
        updateStaticHud();
    }

    private void renderSnapshot(GameSnapshot snapshot) {
        lastSnapshot = snapshot;

        if (graphicsContext != null && snapshot != null) {
            MapVisuals.draw(graphicsContext, snapshot.tiles());
        }

        if (snapshot == null) {
            return;
        }

        if (hpLabel != null) {
            hpLabel.setText("HP: " + snapshot.hp() + "/" + Constants.PLAYER_MAX_HEALTH);
        }
        if (medkitLabel != null) {
            medkitLabel.setText("Medkits: " + snapshot.medkits());
        }
        if (fragmentsLabel != null) {
            String fragmentProgress = snapshot.fragmentsRequired() > 0
                    ? snapshot.fragmentsCollected() + "/" + snapshot.fragmentsRequired()
                    : String.valueOf(snapshot.fragmentsCollected());
            fragmentsLabel.setText("Fragments: " + fragmentProgress);
        }
        if (timeLabel != null) {
            timeLabel.setText("Time: " + formatElapsedTime(snapshot.elapsedMillis()));
        }
        if (messageLabel != null && snapshot.message() != null) {
            messageLabel.setText(snapshot.message());
        }
    }

    private void handleStateChange(GameState state) {
        if (state == GameState.GAME_OVER) {
            showSummaryScene("DefeatScene.fxml");
        } else if (state == GameState.WIN) {
            showSummaryScene("WinScene.fxml");
        }
    }

    private void resizeCanvas() {
        if (canvas == null) {
            return;
        }
        double width = Math.max(mapPane.getWidth(), mapPane.getPrefWidth());
        double height = Math.max(mapPane.getHeight(), mapPane.getPrefHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);

        if (graphicsContext != null && lastSnapshot != null) {
            MapVisuals.draw(graphicsContext, lastSnapshot.tiles());
        }
    }

    private void updateStaticHud() {
        if (roomNameLabel != null && (roomNameLabel.getText() == null || roomNameLabel.getText().isBlank())) {
            roomNameLabel.setText("Red Corridor");
        }
        if (messageLabel != null && (messageLabel.getText() == null || messageLabel.getText().isBlank())) {
            messageLabel.setText("Use WASD/Arrow keys to move.");
        }
    }

    // switch to win/defeat scene
    private void showSummaryScene(String resource) {
        int[][] revealedMap = buildRevealedMap();
        stopEngine();

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

            if (lastSnapshot != null) {
                if (revealedMap != null) {
                    summaryController.setMapData(revealedMap);
                } else {
                    summaryController.setMapData(lastSnapshot.tiles());
                }
                summaryController.setStats(new SummaryStats(
                        lastSnapshot.fragmentsCollected(),
                        lastSnapshot.fragmentsRequired(),
                        lastSnapshot.dronesTriggered(),
                        lastSnapshot.trapsTriggered(),
                        lastSnapshot.elapsedMillis(),
                        lastSnapshot.hp()));
            }

            currentStage.setScene(summaryScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopEngine() {
        if (engine != null) {
            engine.stop();
            engine = null;
        }
    }

    private int[][] buildRevealedMap() {
        if (engine == null) {
            return null;
        }
        World world = engine.getWorld();
        if (world == null) {
            return null;
        }
        int size = Constants.MAP_SIZE;
        int[][] revealed = new int[size][size];
        for (int y = 0; y < size; y++) {
            System.arraycopy(world.map[y], 0, revealed[y], 0, size);
            for (int x = 0; x < size; x++) {
                int hidden = world.hiddenItems[y][x];
                if (hidden != MapVisuals.EMPTY) {
                    revealed[y][x] = hidden;
                }
            }
        }
        return revealed;
    }

    // get minutes and seconds from milliseconds
    private String formatElapsedTime(long elapsedMillis) {
        if (elapsedMillis <= 0) {
            return "--:--";
        }
        long totalSeconds = elapsedMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void lobby() {
        stopEngine();
        super.lobby();
    }

    @Override
    public void quit(javafx.scene.input.KeyEvent event) {
        stopEngine();
        super.quit(event);
    }
}
