package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpSceneController extends Controller {
    private Stage previousStage;
    private Scene previousScene;

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

    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

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
