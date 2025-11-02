package com.team12.redCorridor.utils;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;

// This class helps load images and sound files 
public class ResourceLoader {

    // Load an Image from assets/images/ 
    public static BufferedImage loadImage(String filename) {
        // Try to open the image file
        try (InputStream file = ResourceLoader.class.getResourceAsStream(Constants.IMAGE_PATH + filename)) {

            // If the image file cannot be found, show a message and return null
            if (file == null) {
                System.out.println("Image not found: " + filename);
                return null;
            }

            // Read the image and return it
            return ImageIO.read(file);

        } catch (IOException e) {
            // If something goes wrong while loading, print the error message
            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    //  Load a Sound 
    public static Clip loadSound(String filename) {
        // Try to open and load the sound file
        try (InputStream file = ResourceLoader.class.getResourceAsStream(Constants.SOUND_PATH + filename)) {

            // If the sound file cannot be found, show a message and return null
            if (file == null) {
                System.out.println("Sound not found: " + filename);
                return null;
            }

            // Convert the input stream into an audio stream
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);

            // Create a new Clip (a short sound that can be played)
            Clip clip = AudioSystem.getClip();

            // Load the sound data into the Clip
            clip.open(stream);

            // Return the ready-to-play sound
            return clip;

        } catch (Exception e) {
            // If anything fails, print the error message
            System.out.println("Error loading sound: " + e.getMessage());
            return null;
        }
    }
}
