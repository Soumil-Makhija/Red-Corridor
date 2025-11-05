package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;

public class WinSceneController extends SummarySceneController {
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case N -> newGame(event);
                case L -> lobby();
                default -> {
                }
            }
        });
    }
}
