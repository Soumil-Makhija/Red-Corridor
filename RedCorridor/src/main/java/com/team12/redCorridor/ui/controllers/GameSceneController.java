package com.team12.redCorridor.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.team12.redCorridor.engine.Direction;
import com.team12.redCorridor.engine.EngineListener;
import com.team12.redCorridor.engine.GameEngine;
import com.team12.redCorridor.engine.GameFactory;
import com.team12.redCorridor.engine.GameSnapshot;
import com.team12.redCorridor.engine.GameState;
import com.team12.redCorridor.engine.InputHandler;
import com.team12.redCorridor.entities.Drone;
import com.team12.redCorridor.entities.KeyFragment;
import com.team12.redCorridor.entities.Medkit;
import com.team12.redCorridor.entities.Player;
import com.team12.redCorridor.entities.Position;
import com.team12.redCorridor.ui.util.MapVisuals;
import com.team12.redCorridor.utils.Constants;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for Game Scene
 */
public class GameSceneController extends Controller {
    private static final int MAP_SIZE = 10;
    private static final int HAZARD_DAMAGE = 80;

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
    private Label roomItemsLabel;

    private GameEngine engine;
    private InputHandler input;
    private GameSnapshot lastSnapshot; //cache for summary scenes

    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private final int[][] baseMap = new int[MAP_SIZE][MAP_SIZE]; // static map
    private final int[][] map = new int[MAP_SIZE][MAP_SIZE]; // dynamic map

    private final List<Drone> drones = new ArrayList<>();
    private Player player;
    private int totalFragmentsRequired;
    private int dronesTriggeredCount;
    private int hazardsTriggeredCount;
    private long startTimeMillis;
    private Timeline timerTimeline;

    // get key options from user
    @Override
    public void recordKey(Scene scene) {
    scene.setOnKeyPressed(event -> {
        switch (event.getCode()) {
            case W, UP    -> { if (input != null) input.press(Direction.UP);    }
            case S, DOWN  -> { if (input != null) input.press(Direction.DOWN);  }
            case A, LEFT  -> { if (input != null) input.press(Direction.LEFT);  }
            case D, RIGHT -> { if (input != null) input.press(Direction.RIGHT); }
            case H -> helpMenu();
            case L -> lobby();
            default -> { }
        }
    });
    scene.setOnKeyReleased(event -> {
        switch (event.getCode()) {
            case W, UP    -> { if (input != null) input.release(Direction.UP);    }
            case S, DOWN  -> { if (input != null) input.release(Direction.DOWN);  }
            case A, LEFT  -> { if (input != null) input.release(Direction.LEFT);  }
            case D, RIGHT -> { if (input != null) input.release(Direction.RIGHT); }
            default -> { }
        }
    });
}

    // init game scene
@FXML
public void initialize() {
    // Build world via factory
    GameFactory factory = new GameFactory();
    GameFactory.World world = factory.createDefaultWorld();

    // Canvas setup (keep your existing code)
    canvas = new Canvas(mapPane.getPrefWidth(), mapPane.getPrefHeight());
    graphicsContext = canvas.getGraphicsContext2D();
    mapPane.getChildren().add(canvas);
    mapPane.widthProperty().addListener((obs, o, n) -> resizeCanvas());
    mapPane.heightProperty().addListener((obs, o, n) -> resizeCanvas());

    // Start engine
    input = new InputHandler();
    engine = new GameEngine(world, input, new EngineListener() {
        @Override public void onFrame(GameSnapshot s) {
            lastSnapshot = s;
            javafx.application.Platform.runLater(() -> {
                // draw tiles from engine
                MapVisuals.draw(graphicsContext, s.tiles());
                // HUD
                hpLabel.setText("HP: " + s.hp() + "/" + com.team12.redCorridor.utils.Constants.PLAYER_MAX_HEALTH);
                medkitLabel.setText("Medkits: " + s.medkits());
                String frag = (s.fragmentsRequired() > 0)
                        ? (s.fragmentsCollected() + "/" + s.fragmentsRequired())
                        : Integer.toString(s.fragmentsCollected());
                fragmentsLabel.setText("Fragments: " + frag);
                timeLabel.setText("Time: " + formatElapsedTime(s.elapsedMillis()));
                if (roomNameLabel != null && (roomNameLabel.getText() == null || roomNameLabel.getText().isBlank())) {
                    roomNameLabel.setText("Red Corridor");
                }
                if (roomItemsLabel != null && (roomItemsLabel.getText() == null || roomItemsLabel.getText().isBlank())) {
                    roomItemsLabel.setText("Track items by exploring the map.");
                }
            });
        }
        @Override public void onStateChange(GameState state) {
            javafx.application.Platform.runLater(() -> {
                if (state == GameState.GAME_OVER) showSummaryScene("DefeatScene.fxml");
                if (state == GameState.WIN)       showSummaryScene("WinScene.fxml");
            });
        }
    });
    engine.start();
}

    // add dynamic objects to map (currently hardcoded)
    private void initializeWorldState() {
        player = new Player(new Position(5, 5));
        spawnDronesFromBaseMap();
        dronesTriggeredCount = 0;
        hazardsTriggeredCount = 0;
        startTimeMillis = System.currentTimeMillis();

        int fragmentsOnTiles = countFragmentsOnMap();
        // int fragmentsBehindDoors = registerDoors();
        // totalFragmentsRequired = fragmentsOnTiles + fragmentsBehindDoors;
        totalFragmentsRequired = fragmentsOnTiles;
    }

    // init static objects (currently hard coded)
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

        // hard coded objects
        baseMap[4][6] = MapVisuals.WALL;
        baseMap[4][5] = MapVisuals.WALL;
        baseMap[4][4] = MapVisuals.WALL;
        baseMap[5][4] = MapVisuals.WALL;
        baseMap[6][4] = MapVisuals.WALL;
        baseMap[6][5] = MapVisuals.WALL;
        baseMap[6][6] = MapVisuals.WALL;

        baseMap[2][8] = MapVisuals.FRAGMENT;
        baseMap[1][5] = MapVisuals.FRAGMENT;
        baseMap[1][7] = MapVisuals.FRAGMENT;
        baseMap[2][7] = MapVisuals.FRAGMENT;
        baseMap[5][8] = MapVisuals.FRAGMENT;
        baseMap[7][6] = MapVisuals.FRAGMENT;

        baseMap[2][1] = MapVisuals.MEDKIT;
        baseMap[2][3] = MapVisuals.MEDKIT;
        baseMap[7][3] = MapVisuals.MEDKIT;
        baseMap[7][2] = MapVisuals.MEDKIT;
        baseMap[4][2] = MapVisuals.MEDKIT;

        // baseMap[5][2] = MapVisuals.DOOR;
        // baseMap[7][4] = MapVisuals.DOOR;
        // baseMap[3][7] = MapVisuals.BLAST_DOOR;

        baseMap[3][3] = MapVisuals.DRONE;
        baseMap[3][4] = MapVisuals.DRONE;
        baseMap[3][5] = MapVisuals.DRONE;

        baseMap[1][1] = MapVisuals.EXIT;
        baseMap[8][8] = MapVisuals.DANGER;
    }

    // get total fragments
    private int countFragmentsOnMap() {
        int count = 0;
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                if (baseMap[y][x] == MapVisuals.FRAGMENT) {
                    count++;
                }
            }
        }
        return count;
    }

    // add drones to map
    private void spawnDronesFromBaseMap() {
        drones.clear();
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                if (baseMap[y][x] == MapVisuals.DRONE) {
                    drones.add(new Drone(new Position(x, y)));
                    baseMap[y][x] = MapVisuals.EMPTY;
                }
            }
        }
    }

    private void resizeCanvas() {
        double width = Math.max(mapPane.getWidth(), mapPane.getPrefWidth());
        double height = Math.max(mapPane.getHeight(), mapPane.getPrefHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);
        drawMap();
    }

    // display icons on map
    private void drawMap() {
        if (graphicsContext == null) {
            return;
        }
        MapVisuals.draw(graphicsContext, map);
    }

    // handles player movements
    public void moveUp() {
        attemptMove(playerX(), playerY() - 1);
    }

    public void moveDown() {
        attemptMove(playerX(), playerY() + 1);
    }

    public void moveLeft() {
        attemptMove(playerX() - 1, playerY());
    }

    public void moveRight() {
        attemptMove(playerX() + 1, playerY());
    }

    private void attemptMove(int newX, int newY) {
        if (!isWithinBounds(newX, newY) || !isWalkable(newX, newY)) {
            return;
        }

        // get object at that tile
        int tileValue = baseMap[newY][newX];
        if (tileValue == MapVisuals.WALL) {
            return;
        }
        if (tileValue == MapVisuals.EXIT && !hasCollectedAllFragments()) {
            updateHud("Exit locked. Collect all fragments to unlock");
            return;
        }

        // update player pos
        player.getPosition().setX(newX);
        player.getPosition().setY(newY);

        String interactionMessage = handleTileInteraction(newX, newY, tileValue); // action handler
        int droneDamage = moveDronesTowardPlayer(); // move drones after player move
        String statusMessage = droneDamage > 0 ? "Drone caught you! -" + droneDamage + " HP" : interactionMessage;

        refreshDynamicMap(); // update pos
        updateHud(statusMessage); // update hud message
        drawMap(); // update icons
        evaluateGameState(); // check end game conditions
    }

    // handles action for different objects
    private String handleTileInteraction(int x, int y, int tile) {
        return switch (tile) {
            case MapVisuals.FRAGMENT -> collectFragmentAt(new Position(x, y), "Fragment");
            case MapVisuals.MEDKIT -> applyMedkit(new Position(x, y));
            case MapVisuals.DANGER -> triggerHazard(new Position(x, y));
            // case MapVisuals.DOOR, MapVisuals.BLAST_DOOR -> handleDoorInteraction(x, y);
            case MapVisuals.EXIT -> hasCollectedAllFragments() ? "Exit door unlocked." : null;
            default -> null;
        };
    }

    // handles player collecting fragment
    private String collectFragmentAt(Position position, String sourceLabel) {
        // update players inventory
        KeyFragment fragment = new KeyFragment(new Position(position.getX(), position.getY()));
        fragment.collect();
        player.addFragment(fragment);
        baseMap[position.getY()][position.getX()] = MapVisuals.EMPTY; // remove fragment

        return "Fragment collected";
    }

    /*
     * change this to applying medkit later
     */
    // handles player collecting medkit
    private String applyMedkit(Position position) {
        // update players inventory
        Medkit medkit = new Medkit(new Position(position.getX(), position.getY()));
        player.addMedkit();
        player.heal(medkit.getHealAmount());
        baseMap[position.getY()][position.getX()] = MapVisuals.EMPTY; // remove medkit
        return "Medkit restored +" + medkit.getHealAmount() + " HP";
    }

    // handles traps trigger
    private String triggerHazard(Position position) {
        hazardsTriggeredCount++;
        baseMap[position.getY()][position.getX()] = MapVisuals.EMPTY;
        player.takeDamage(HAZARD_DAMAGE);
        return "Hazard triggered! -" + HAZARD_DAMAGE + " HP";
    }

    // validate all fragments collected
    private boolean hasCollectedAllFragments() {
        if (totalFragmentsRequired <= 0) {
            return true;
        }
        return player.getFragmentCount() >= totalFragmentsRequired;
    }

    // handles drones movement
    private int moveDronesTowardPlayer() {
        if (drones.isEmpty()) {
            return 0;
        }

        int totalDamage = 0;
        for (Drone drone : drones) {
            if (!drone.isActive()) { // check drones status
                continue;
            }

            Position dronePosition = drone.getPosition();
            int originalX = dronePosition.getX();
            int originalY = dronePosition.getY();

            drone.moveToward(player.getPosition()); // movement

            // validate move
            int targetX = dronePosition.getX();
            int targetY = dronePosition.getY();
            if (!isWithinBounds(targetX, targetY) || !isWalkable(targetX, targetY)) {
                dronePosition.setX(originalX);
                dronePosition.setY(originalY);
            }

            // if drone hit player
            if (dronePosition.getX() == playerX() && dronePosition.getY() == playerY()) {
                player.takeDamage(drone.getDamage());
                drone.deactivate();
                dronesTriggeredCount++;
                totalDamage += drone.getDamage();
            }
        }
        return totalDamage;
    }

    // update drones and players pos
    private void refreshDynamicMap() {
        for (int y = 0; y < MAP_SIZE; y++) {
            System.arraycopy(baseMap[y], 0, map[y], 0, MAP_SIZE);
        }

        for (Drone drone : drones) {
            if (drone.isActive()) {
                Position pos = drone.getPosition();
                map[pos.getY()][pos.getX()] = MapVisuals.DRONE;
            }
        }

        map[playerY()][playerX()] = MapVisuals.PLAYER;
    }

    // check end game conditions
    private void evaluateGameState() {
        if (player.getHealth() <= 0) {
            showSummaryScene("DefeatScene.fxml");
            return;
        }

        // hard coded
        if (playerX() == 1 && playerY() == 1 && hasCollectedAllFragments()) {
            showSummaryScene("WinScene.fxml");
        }
    }

    // switch to win/defeat scene
    private void showSummaryScene(String resource) {
    if (engine != null) engine.stop();

    Stage currentStage = stage != null ? stage : getPrimaryStage();
    if (currentStage == null) return;

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
                lastSnapshot.hp()
            ));
        }

        currentStage.setScene(summaryScene);
        currentStage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private int[][] copyMapData() {
        refreshDynamicMap();
        int[][] copy = new int[MAP_SIZE][MAP_SIZE];
        for (int y = 0; y < MAP_SIZE; y++) {
            System.arraycopy(map[y], 0, copy[y], 0, MAP_SIZE);
        }
        return copy;
    }

    // validating move methods
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE;
    }

    private boolean isWalkable(int x, int y) {
        return baseMap[y][x] != MapVisuals.WALL;
    }

    private int playerX() {
        return player.getPosition().getX();
    }

    private int playerY() {
        return player.getPosition().getY();
    }

    // update hud message
    private void updateHud(String newMessage) {
        if (hpLabel != null) {
            hpLabel.setText("HP: " + player.getHealth() + "/" + Constants.PLAYER_MAX_HEALTH);
        }
        if (medkitLabel != null) {
            medkitLabel.setText("Medkits: " + player.getMedkitCount());
        }
        if (fragmentsLabel != null) {
            String fragmentProgress = totalFragmentsRequired > 0
                    ? player.getFragmentCount() + "/" + totalFragmentsRequired
                    : String.valueOf(player.getFragmentCount());
            fragmentsLabel.setText("Fragments: " + fragmentProgress);
        }
        updateTimeLabel();
        if (roomNameLabel != null && (roomNameLabel.getText() == null || roomNameLabel.getText().isBlank())) {
            roomNameLabel.setText("Red Corridor");
        }
        if (roomItemsLabel != null && (roomItemsLabel.getText() == null || roomItemsLabel.getText().isBlank())) {
            roomItemsLabel.setText("Track items by exploring the map.");
        }
        if (newMessage != null && messageLabel != null) {
            messageLabel.setText(newMessage);
        } else if (messageLabel != null && messageLabel.getText() == null) {
            messageLabel.setText("");
        }
    }

    // handles timer for each game
    private void startTimer() {
        stopTimer();
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimeLabel()));
        timerTimeline.setCycleCount(Timeline.INDEFINITE); // repeat indefinitely
        timerTimeline.playFromStart();
        updateTimeLabel(); //
    }

    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
            timerTimeline = null;
        }
    }

    // update label in game scene
    private void updateTimeLabel() {
        if (timeLabel == null) {
            return;
        }
        timeLabel.setText("Time: " + formatElapsedTime(elapsedMillis()));
    }

    // update summary stats to display
    private SummaryStats buildSummaryStats() {
        return new SummaryStats(
                player.getFragmentCount(),
                totalFragmentsRequired,
                dronesTriggeredCount,
                hazardsTriggeredCount,
                elapsedMillis(),
                player.getHealth());
    }

    // store time in milliseconds
    private long elapsedMillis() {
        return startTimeMillis > 0 ? System.currentTimeMillis() - startTimeMillis : 0;
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
    if (engine != null) engine.stop();   // stop engine when returning to lobby
    super.lobby();                       // call parent method
}

@Override
public void quit(javafx.scene.input.KeyEvent event) {
    if (engine != null) engine.stop();   // stop engine when quitting the program
    super.quit(event);                   // call parent quit logic
    }
}