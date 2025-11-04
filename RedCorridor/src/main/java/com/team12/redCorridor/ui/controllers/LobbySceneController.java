package com.team12.redCorridor.ui.controllers;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class LobbySceneController extends Controller {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // Replace with game-specific handling instead of printing
                KeyCode e = event.getCode();

                switch (e) {
                    case H -> helpMenu();
                    case Q -> quit();
                    case N -> newGame(event);
                    case W -> moveUp();
                    case UP -> moveUp();
                    case S -> moveDown();
                    case DOWN -> moveDown();
                    case A -> moveLeft();
                    case LEFT -> moveLeft();
                    case D -> moveRight();
                    case RIGHT -> moveRight();
                    default -> System.out.println(e);
                }
            }
        });
    }

    public void moveUp() {
        System.out.println("Up");
    }

    public void moveDown() {
        System.out.println("Down");
    }

    public void moveLeft() {
        System.out.println("Left");
    }

    public void moveRight() {
        System.out.println("Right");
    }

    public void newGame(KeyEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/team12/redCorridor/ui/scenes/GameScene.fxml"));

            root = loader.load();
            GameSceneController controller = loader.getController();
            Stage currentStage = (Stage) ((Scene) event.getSource()).getWindow();
            Scene scene = new Scene(root);
            controller.recordKey(scene);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
