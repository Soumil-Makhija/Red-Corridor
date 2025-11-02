package com.team12.redCorridor.utils;

import java.io.*;

// This class helps save and load objects (like player data or game state) to files
public class SaveManager {

    //  Save any object to a file 
    public static void saveObject(Object obj, String filepath) {
        // Try to write the given object to the specified file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(obj); // Write the object data to the file
            System.out.println("Saved successfully to " + filepath);
        } catch (IOException e) {
            // If something goes wrong while saving, show an error message
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Load any object from a file 
    public static Object loadObject(String filepath) {
        // Try to read the saved object from the specified file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            return in.readObject(); // Return the loaded object
        } catch (IOException | ClassNotFoundException e) {
            // If the file doesn't exist or can't be read, print an error message
            System.out.println("Error loading file: " + e.getMessage());
            return null; // Return null if loading fails
        }
    }
}
