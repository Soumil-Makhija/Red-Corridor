package com.team12.redCorridor.ui.controllers;

import javafx.scene.Scene;

public class Controller {
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case H -> helpMenu();
                case Q -> quit();
                default -> {
                }
            }
        });
    }

    public void helpMenu() {
        System.out.println("switch to help menu");
    }

    public void quit() {
        System.out.println("switch to lobby");
    }
}
