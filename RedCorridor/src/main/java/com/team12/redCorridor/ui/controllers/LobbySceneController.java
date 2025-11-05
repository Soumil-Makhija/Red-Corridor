package com.team12.redCorridor.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LobbySceneController extends Controller {
    @FXML
    private Pane lobbyPane;

    private Stage stage;

    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case H -> helpMenu();
                case Q -> quit(event);
                case N -> newGame(event);
                default -> {
                }
            }
        });
    }

    public void quit(KeyEvent event) {
        stage = (Stage) lobbyPane.getScene().getWindow();
        stage.close();
    }
}
