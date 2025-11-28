Red Corridor is a top down sci-fi maze escape game in which the player navigates a series of 
interconnected rooms, avoids hazards, and reaches the exit before being caught. 
The game is built using Java, JavaFX, and Maven.

Description:
Red Corridor is a modular and test driven JavaFX game focused on movement, navigation, and 
timing. The player explores maze based rooms filled with enemies and traps, while the game 
engine handles movement, collision detection, path loading, and rendering. The project includes 
a complete JavaFX interface, automated tests, a game engine package, and Maven generated artifacts 
such as JAR files, Javadoc, and coverage reports.

Getting Started:
Dependencies
The following software is required before running the game:
  - Java 21
  - Apache Maven 3.9 or higher
  - macOS, Windows, or Linux
JavaFX is handled automatically by Maven and requires no manual installation.
To verify your setup, you may run:
 java -version
 mvn -version
 
Installing:
1. Clone or download the repository.
2. Open a terminal inside the RedCorridor project folder.
3. Build the project by running:
    - mvn clean install
This command compiles the entire project, runs all tests, and produces build artifacts in the target directory.

Executing the Program:
To run the game, enter the following command from the project root:
  - mvn javafx:run
For a clean rebuild and run:
  - mvn clean javafx:run
All maze files, icons, and UI scenes used by the game are stored in:
 src/main/resources/com/team12/redCorridor/

Artifacts:
Game JAR
To generate the JAR file, run:
  - mvn package
The JAR will be created in the directory:
 target/redCorridor-1.0-SNAPSHOT.jar
If a fat JAR is also generated, it will appear as:
  - target/*-jar-with-dependencies.jar
A fat JAR can be launched with:
  - java -jar <jar-name>.jar

Javadoc:
To create the project’s Javadoc documentation, run:
  - mvn javadoc:javadoc
The documentation can be opened from:
  - target/site/apidocs/index.html

Test Suite:
To execute the full test suite, run:
  - mvn clean test
Test results will be stored in:
  - target/surefire-reports/
A JaCoCo code coverage report is automatically generated in:
  - target/site/jacoco/index.html

Gameplay Overview:
Players move using W, A, S, D, or the arrow keys. The objective is to navigate through the maze, 
avoid enemies and environmental hazards, and reach the exit room. Colliding with an enemy results 
in game over. Maze layouts, scenes, and icons are located in the project’s main resources directory.
