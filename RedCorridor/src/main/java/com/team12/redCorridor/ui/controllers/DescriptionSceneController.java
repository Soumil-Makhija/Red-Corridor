package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;

/**
 * Controller for the Description Scene.
 */
public class DescriptionSceneController extends Controller {
    @Override
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case B -> lobby();
                default -> {
                }
            }
        });
    }
}
