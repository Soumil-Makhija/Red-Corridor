package com.team12.redCorridor.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Loads 15x15 maze layouts stored under resources. Each maze uses '#' for
 * walls, 'E' for exit, and spaces for walkable cells.
 */
public class MazeLoader {
    private static final String BASE_PATH = "/com/team12/redCorridor/ui/mazes_15x15_rooms_open_txt/";
    private static final String[] MAZE_FILES = {
            "maze_rooms_open_1.txt",
            "maze_rooms_open_2.txt",
            "maze_rooms_open_3.txt",
            "maze_rooms_open_4.txt",
            "maze_rooms_open_5.txt",
            "maze_rooms_open_6.txt",
            "maze_rooms_open_7.txt",
            "maze_rooms_open_8.txt",
            "maze_rooms_open_9.txt",
            "maze_rooms_open_10.txt"
    };
    private static final int MAZE_SIZE = 15;

    private final Random random;

    public MazeLoader() {
        this(new Random());
    }

    public MazeLoader(Random random) {
        this.random = random;
    }

    public char[][] loadRandomMaze() {
        String file = MAZE_FILES[random.nextInt(MAZE_FILES.length)];
        System.out.println("[MazeLoader] Loading maze: " + file);
        return loadMaze(file);
    }

    public char[][] loadMaze(String fileName) {
        try (InputStream is = MazeLoader.class.getResourceAsStream(BASE_PATH + fileName)) {
            if (is == null) {
                throw new IllegalArgumentException("Maze file not found: " + fileName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                char[][] grid = new char[MAZE_SIZE][MAZE_SIZE];
                String line;
                int row = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (row >= MAZE_SIZE) {
                        throw new IllegalStateException("Maze " + fileName + " has more than " + MAZE_SIZE + " rows.");
                    }
                    if (line.length() < MAZE_SIZE) {
                        throw new IllegalStateException(
                                "Row " + row + " in maze " + fileName + " is shorter than " + MAZE_SIZE + " characters.");
                    }
                    for (int col = 0; col < MAZE_SIZE; col++) {
                        grid[row][col] = line.charAt(col);
                    }
                    row++;
                }
                if (row != MAZE_SIZE) {
                    throw new IllegalStateException(
                            "Maze " + fileName + " must contain exactly " + MAZE_SIZE + " rows but had " + row + ".");
                }
                return grid;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load maze " + fileName, e);
        }
    }
}
