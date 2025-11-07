package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;

/**
 * Controller for Lobby Scene
 */
public class LobbySceneController extends Controller {
    // get key options from user
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
}
