package com.team12.redCorridor.ui.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
// import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Abstract Controller for all scenes
 */
public abstract class Controller {
    protected Stage stage;
    private static Stage primaryStage;

    public void setStage(Stage stage) {
        this.stage = stage;
        if (primaryStage == null && stage != null) {
            primaryStage = stage;
        }
    }

    protected Stage getPrimaryStage() {
        return primaryStage != null ? primaryStage : stage;
    }

    // get key options from user
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case H -> helpMenu();
                case L -> lobby();
                default -> {
                }
            }
        });
    }

    // switch scene to help menu (new window)
    public void helpMenu() {
        Stage ownerStage = stage != null ? stage : getPrimaryStage();
        if (ownerStage == null) {
            return;
        }

        try {
            // getting scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/HelpScene.fxml"));
            Parent root = loader.load();

            HelpSceneController controller = loader.getController();
            Stage helpStage = new Stage();
            Scene helpScene = new Scene(root);

            // set previous stage to go back
            controller.setStage(helpStage);
            controller.setPreviousStage(ownerStage);
            controller.setPreviousScene(ownerStage.getScene());
            controller.recordKey(helpScene);

            // new window
            helpStage.setTitle("Help");
            helpStage.initOwner(ownerStage);
            // helpStage.initModality(Modality.WINDOW_MODAL);
            helpStage.setScene(helpScene);
            helpStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // switch to lobby scene
    public void lobby() {
        Stage targetStage = getPrimaryStage();
        if (targetStage == null) {
            return;
        }

        if (stage != null && stage != targetStage) {
            stage.close();
        }

        try {
            // get lobby scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/LobbyScene.fxml"));
            Parent root = loader.load();
            LobbySceneController lobbyController = loader.getController(); // get controller
            Scene lobbyScene = new Scene(root);

            targetStage.setScene(lobbyScene);
            lobbyController.setStage(targetStage);
            lobbyController.recordKey(lobbyScene);
            targetStage.sizeToScene();
            targetStage.centerOnScreen();
            targetStage.show();
            targetStage.toFront();
            targetStage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // switching to new game scene
    public void newGame(KeyEvent event) {
        try {
            // get game scene
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/GameScene.fxml"));

            Parent root = loader.load();
            GameSceneController controller = loader.getController(); // game scene controller
            // new game on same window
            Stage currentStage = stage != null ? stage : (Stage) ((Scene) event.getSource()).getWindow();
            Scene gameScene = new Scene(root);

            controller.setStage(currentStage);
            controller.recordKey(gameScene);
            currentStage.setScene(gameScene);
            currentStage.sizeToScene();
            currentStage.centerOnScreen();
            currentStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // program exit
    public void quit(KeyEvent event) {
        Stage currentStage = stage != null ? stage : (Stage) ((Scene) event.getSource()).getWindow();
        if (currentStage != null) {
            currentStage.close();
        }
    }
}
