package com.team12.redCorridor.ui.controllers;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Controller {
    public void recordKey(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // Replace with game-specific handling instead of printing
                KeyCode e = event.getCode();

                switch (e) {
                    case H -> helpMenu();
                    case Q -> quit();
                    default -> System.out.println(e);
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
