package com.team12.redCorridor.engine;

import static org.junit.Assert.*;

import org.junit.Test;

import com.team12.redCorridor.utils.Constants;

public class MazeLoaderTest {

    @Test
    public void loadSpecificMazeReturnsValidGrid() {
        MazeLoader loader = new MazeLoader();
        char[][] grid = loader.loadMaze("maze_rooms_open_1.txt");

        assertNotNull(grid);
        assertEquals(Constants.MAP_SIZE, grid.length);

        for (int row = 0; row < Constants.MAP_SIZE; row++) {
            assertEquals(Constants.MAP_SIZE, grid[row].length);
        }

        boolean hasExit = false;
        for (int y = 0; y < Constants.MAP_SIZE; y++) {
            for (int x = 0; x < Constants.MAP_SIZE; x++) {
                if (grid[y][x] == 'E') {
                    hasExit = true;
                    break;
                }
            }
            if (hasExit) {
                break;
            }
        }
        assertTrue("Maze should contain at least one exit 'E'", hasExit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadingMissingMazeThrowsException() {
        MazeLoader loader = new MazeLoader();
        loader.loadMaze("non_existing_maze.txt");
    }

    @Test
    public void loadRandomMazeStillReturnsValidSize() {
        MazeLoader loader = new MazeLoader();
        char[][] grid = loader.loadRandomMaze();

        assertNotNull(grid);
        assertEquals(Constants.MAP_SIZE, grid.length);
        for (int row = 0; row < Constants.MAP_SIZE; row++) {
            assertEquals(Constants.MAP_SIZE, grid[row].length);
        }
    }
}
