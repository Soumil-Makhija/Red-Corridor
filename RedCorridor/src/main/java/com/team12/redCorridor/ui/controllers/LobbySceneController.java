package com.team12.redCorridor.ui.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.team12.redCorridor.engine.GameMode;
import com.team12.redCorridor.engine.GameSettings;
import com.team12.redCorridor.utils.Constants;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Controller for Lobby Scene
 */
public class LobbySceneController extends Controller {
    // Labels holder
    @FXML
    private Label mapSizeValueLabel;
    @FXML
    private Label fragmentsValueLabel;
    @FXML
    private Label medkitsValueLabel;
    @FXML
    private Label trapsValueLabel;
    @FXML
    private Label droneRoomsValueLabel;
    @FXML
    private Label emptyRoomsValueLabel;
    @FXML
    private Label droneDamageValueLabel;
    @FXML
    private Label trapDamageValueLabel;
    @FXML
    private Label medkitHealValueLabel;
    @FXML
    private Label easyModeLabel;
    @FXML
    private Label mediumModeLabel;
    @FXML
    private Label hardModeLabel;
    @FXML
    private Label extremeModeLabel;

    private List<Label> modeLabels;
    private final GameMode[] modes = { GameMode.EASY, GameMode.MEDIUM, GameMode.HARD, GameMode.EXTREME };
    private int selectedIndex = 1; // default choice

    @FXML
    public void initialize() {
        modeLabels = List.of(easyModeLabel, mediumModeLabel, hardModeLabel, extremeModeLabel);
        GameMode current = GameSettings.getCurrentMode(); // load default mode
        int idx = Arrays.asList(modes).indexOf(current);
        if (idx >= 0) {
            selectedIndex = idx;
        }
        updateModeState();
    }

    // get key options from user
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W, UP -> moveSelection(-1);
                case S, DOWN -> moveSelection(1);
                case ENTER, N -> startSelectedMode(event);
                case D -> description();
                case H -> helpMenu();
                case Q -> quit(event);
                default -> {
                }
            }
        });
    }

    private void description() {
        Stage targetStage = getPrimaryStage();
        if (targetStage == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/DescriptionScene.fxml"));
            Parent root = loader.load();
            DescriptionSceneController controller = loader.getController();
            Scene scene = new Scene(root);

            controller.setStage(targetStage);
            controller.recordKey(scene);
            targetStage.setScene(scene);
            targetStage.sizeToScene();
            targetStage.centerOnScreen();
            targetStage.show();
            targetStage.toFront();
            targetStage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveSelection(int delta) {
        selectedIndex = (selectedIndex + delta + modes.length) % modes.length;
        updateModeState();
    }

    private void updateModeState() {
        GameMode mode = modes[selectedIndex];
        updateModeLabels();
        updateConfigLabels(mode);
    }

    private void updateModeLabels() {
        for (int i = 0; i < modeLabels.size(); i++) {
            Label label = modeLabels.get(i);
            if (label == null) {
                continue;
            }
            if (i == selectedIndex) {
                label.setStyle("-fx-font-weight: bold; -fx-underline: true;"); // focus on current selection
            } else {
                label.setStyle("");
            }
        }
    }

    // update world settings
    private void updateConfigLabels(GameMode mode) {
        mapSizeValueLabel.setText(String.valueOf(Constants.MAP_SIZE));
        fragmentsValueLabel.setText(String.valueOf(mode.fragments()));
        medkitsValueLabel.setText(String.valueOf(mode.medkits()));
        trapsValueLabel.setText(String.valueOf(mode.traps()));
        droneRoomsValueLabel.setText(String.valueOf(mode.droneRooms()));
        emptyRoomsValueLabel.setText(String.valueOf(mode.emptyRooms()));
        droneDamageValueLabel.setText(String.valueOf(Constants.DRONE_DAMAGE));
        trapDamageValueLabel.setText(String.valueOf(Constants.DAMAGE));
        medkitHealValueLabel.setText(String.valueOf(Constants.MEDKIT_HEAL_AMOUNT));
    }

    private void startSelectedMode(KeyEvent event) {
        GameSettings.setMode(modes[selectedIndex]);
        newGame(event);
    }
}
