package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import org.junit.Test;

public class InputHandlerTest {

    @Test
    public void pressAndReleaseUpdatePressedState() {
        InputHandler handler = new InputHandler();

        assertFalse(handler.isPressed(Direction.UP));

        handler.press(Direction.UP);
        assertTrue(handler.isPressed(Direction.UP));

        handler.release(Direction.UP);
        assertFalse(handler.isPressed(Direction.UP));
    }

   
    @Test
    public void requestAndConsumeMedkitUseWorksOnce() {
        InputHandler handler = new InputHandler();

        handler.requestMedkitUse();
        assertTrue(handler.consumeMedkitRequest());

        assertFalse(handler.consumeMedkitRequest());
    }
}
