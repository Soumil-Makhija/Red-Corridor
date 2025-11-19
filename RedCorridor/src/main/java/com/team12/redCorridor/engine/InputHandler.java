package com.team12.redCorridor.engine;

import java.util.EnumSet;

/**
 * Thread-safe simple input tracker.
 * - press(Direction): mark a direction as currently pressed
 * - release(Direction): mark it released
 * - isPressed(Direction): query current pressed state
 *
 * GUI code should call press/release from KeyListener callbacks.
 */
public class InputHandler {
    private final EnumSet<Direction> pressed = EnumSet.noneOf(Direction.class);
    private boolean medkitRequested;

    public synchronized void press(Direction d) {
        pressed.add(d);
    }

    public synchronized void release(Direction d) {
        pressed.remove(d);
    }

    public synchronized boolean isPressed(Direction d) {
        return pressed.contains(d);
    }

    public synchronized void clear() {
        pressed.clear();
        medkitRequested = false;
    }

    public synchronized void requestMedkitUse() {
        medkitRequested = true;
    }

    public synchronized boolean consumeMedkitRequest() {
        boolean requested = medkitRequested;
        medkitRequested = false;
        return requested;
    }
}
