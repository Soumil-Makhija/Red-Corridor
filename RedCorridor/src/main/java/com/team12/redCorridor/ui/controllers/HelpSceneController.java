package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for Help Menu Scene
 */
public class HelpSceneController extends Controller {
    private Stage previousStage;
    private Scene previousScene;

    // get key options from user
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case B -> back();
                case L -> lobby();
                default -> {
                }
            }
        });
    }

    // store previous stage and scene to go back
    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    // switching back to previous stage and scene
    private void back() {
        if (stage != null) {
            stage.close();
        }

        if (previousStage != null) {
            if (previousScene != null && previousStage.getScene() != previousScene) {
                previousStage.setScene(previousScene);
            }
            previousStage.show();
            previousStage.toFront();
            previousStage.requestFocus();
        }
    }
}
