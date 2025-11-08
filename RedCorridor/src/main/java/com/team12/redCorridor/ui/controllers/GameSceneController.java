package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import com.team12.redCorridor.engine.Direction;
import com.team12.redCorridor.engine.EngineListener;
import com.team12.redCorridor.engine.GameEngine;
import com.team12.redCorridor.engine.GameFactory;
import com.team12.redCorridor.engine.GameSnapshot;
import com.team12.redCorridor.engine.GameState;
import com.team12.redCorridor.engine.InputHandler;
import com.team12.redCorridor.ui.util.MapVisuals;
import com.team12.redCorridor.utils.Constants;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    @FXML
    private javafx.scene.layout.VBox roomItemsContainer;

    private GameEngine engine;
    private InputHandler input;
    private GameSnapshot lastSnapshot;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private int lastPlayerX = -1;
    private int lastPlayerY = -1;

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
        GameFactory.World world = new GameFactory().createDefaultWorld();
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

        // Update room items whenever the snapshot changes
        updateRoomItems(snapshot);

        // Track player position
        lastPlayerX = snapshot.playerX();
        lastPlayerY = snapshot.playerY();

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

    private void updateRoomItems(GameSnapshot snapshot) {
        if (roomItemsContainer == null || snapshot == null) {
            return;
        }

        roomItemsContainer.getChildren().clear();

        int px = snapshot.playerX();
        int py = snapshot.playerY();
        int[][] tiles = snapshot.tiles();

        if (px < 0 || py < 0 || py >= tiles.length || px >= tiles[0].length) {
            return;
        }

        int tileType = tiles[py][px];

        // Show items for specific tile types (including items that were just revealed
        // from doors)
        if (tileType == MapVisuals.MEDKIT || tileType == MapVisuals.FRAGMENT ||
                tileType == MapVisuals.DANGER || tileType == MapVisuals.DOOR) {

            HBox itemBox = new HBox(8.0);
            itemBox.setAlignment(Pos.CENTER_LEFT);

            Image icon = MapVisuals.getIconForTileType(tileType);
            if (icon != null) {
                ImageView iconView = new ImageView(icon);
                iconView.setFitWidth(32);
                iconView.setFitHeight(32);
                iconView.setPreserveRatio(true);
                itemBox.getChildren().add(iconView);
            }

            Label itemLabel = new Label(MapVisuals.getNameForTileType(tileType));
            itemBox.getChildren().add(itemLabel);

            roomItemsContainer.getChildren().add(itemBox);
        } else if (tileType == MapVisuals.OPEN_DOOR) {
            // Door already opened, show nothing or "Already explored"
            Label emptyLabel = new Label("Door already opened");
            emptyLabel.setStyle("-fx-text-fill: gray;");
            roomItemsContainer.getChildren().add(emptyLabel);
        } else {
            Label emptyLabel = new Label("No items here");
            emptyLabel.setStyle("-fx-text-fill: gray;");
            roomItemsContainer.getChildren().add(emptyLabel);
        }
    }

    // switch to win/defeat scene
    private void showSummaryScene(String resource) {
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
                summaryController.setMapData(lastSnapshot.tiles());
                summaryController.setStats(new SummaryStats(
                        lastSnapshot.fragmentsCollected(),
                        lastSnapshot.fragmentsRequired(),
                        lastSnapshot.dronesTriggered(),
                        lastSnapshot.hazardsTriggered(),
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
