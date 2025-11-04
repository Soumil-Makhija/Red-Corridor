# Work Division

## Project Structure

``` txt
src/
├── entities/       → Person 1
├── utils/          → Person 1
├── map/            → Person 2
├── ui/             → Person 3
├── engine/         → Person 4
└── Main.java       → Person 4
```

---

## Person 1: Entities & Utils (Game Objects & Utilities)

### Directories: `src/entities/` and `src/utils/`

### Files to Create (7 files)

**Entities Package:**

- [ ] `Player.java`
- [ ] `Drone.java`
- [ ] `KeyFragment.java`
- [ ] `Medkit.java`

**Utils Package:**

- [ ] `Constants.java` (all game constants)
- [ ] `ResourceLoader.java` (load images, sounds, maps)
- [ ] `SaveManager.java` (save/load game state)

### Responsibilities

**Entities:**

- Player movement, health, inventory management
- Drone patrol behavior and detection
- Collectible items (key fragments, medkits)
- Character states and properties

**Utils:**

- Constants and configuration
- Resource loading (images, sounds, levels)
- Save/load functionality

### What to Implement

- Player position, health, key collection
- Drone movement patterns and player detection
- Item collection logic
- Getter/setter methods for all properties
- All game constants (screen size, speeds, paths)
- Image and sound loading from files
- Game state serialization

### What NOT to Do

- ❌ No map/tile logic
- ❌ No UI/rendering code (just provide resources)
- ❌ No collision detection (just provide positions)
- ❌ No game loop management

### Dependencies

- Uses: `Position` (from Person 2), `Direction` (from Person 4)
- Used by: Everyone (entities and utils are used by all other packages)

---

## Person 2: Map Package (World, Tiles, Obstacles)

### Directory: `src/map/`

### Files to Create (11 files)

- [ ] `Map.java`
- [ ] `Location.java` (abstract base class)
- [ ] `Position.java`
- [ ] `AlarmNode.java`
- [ ] `Wall.java`
- [ ] `Door.java`
- [ ] `BlastDoor.java`
- [ ] `ExitDoor.java`
- [ ] `Trap.java`
- [ ] `Laser.java`
- [ ] `RadLeak.java`

### Responsibilities

- Map grid structure and management
- All tile types (walls, doors, traps, hazards)
- Location interactions (what happens when player steps on tile)
- Alarm system
- Map file loading

### What to Implement

- Map as 2D grid of Location objects
- Each tile type with its properties
- Door locking/unlocking logic
- Trap activation
- Radiation damage zones
- Alarm triggering

### What NOT to Do

- ❌ No player movement logic
- ❌ No UI/rendering
- ❌ No game loop
- ❌ No input handling

### Dependencies

- Uses: `Player`, `Drone` (from Person 1), `Direction` (from Person 4)
- Used by: Person 3 (rendering map), Person 4 (game engine)

---

## Person 3: UI Package (Graphics & User Interface)

### Directory: `src/ui/`

### Files to Create (6 files)

- [ ] `GameUI.java` (main JavaFX application)
- [ ] `GamePanel.java` (game rendering panel)
- [ ] `MenuScreen.java`
- [ ] `GameOverScreen.java`
- [ ] `HUD.java` (health bar, score, keys)
- [ ] `Renderer.java` (draws all game objects)

### Responsibilities

- Window management (JavaFX setup)
- Game rendering loop (60 FPS)
- All screens (menu, game, pause, game over)
- HUD elements (health bar, key count, score)
- Drawing player, drones, map tiles, items
- Visual effects and animations

### What to Implement

- JavaFX setup
- Canvas rendering
- Menu navigation
- HUD display
- Render methods for all game objects
- Screen transitions

### What NOT to Do

- ❌ No game logic (just display what you're given)
- ❌ No collision detection
- ❌ No entity AI
- ❌ No input handling (Person 4 handles that)

### Dependencies

- Uses: All classes from Person 1 & 2 (for rendering)
- Used by: Person 4 (game engine calls update/render)

---

## Person 4: Engine (Game Systems & Integration)

### Directory: `src/engine/` and `Main.java`

### Files to Create (7 files)

**Engine Package:**

- [ ] `GameEngine.java` (main game loop controller)
- [ ] `GameFactory.java` (creates map, entities)
- [ ] `Direction.java` (enum: UP, DOWN, LEFT, RIGHT)
- [ ] `GameState.java` (enum: MENU, PLAYING, PAUSED, GAME_OVER, WIN)
- [ ] `InputHandler.java` (keyboard input)
- [ ] `CollisionHandler.java` (collision detection)

**Root:**

- [ ] `Main.java` (application entry point)

### Responsibilities

- Game engine (update all entities, check win/lose)
- Factory pattern (create maps and entities)
- Input handling (keyboard events)
- Collision detection system
- All enums
- Main application startup
- **Integration of all components**

### What to Implement

- Game loop logic (update entities, check collisions)
- Player movement based on input
- Win/lose condition checking
- Level loading via factory
- Collision between player/drones, player/items
- Putting all pieces together
- Connect UI, entities, and map

### What NOT to Do

- ❌ Don't reimplement entity behavior (call their methods)
- ❌ Don't reimplement rendering (call UI methods)
- ❌ Don't reimplement constants (use Person 1's Constants class)

### Dependencies

- Uses: Everything (integrates all components)
- This is the "glue" that connects Person 1, 2, and 3's work

---

## Quick Reference: Who Does What?

| Task | Person |
|------|--------|
| Player health/movement | Person 1 |
| Drone AI | Person 1 |
| Collecting items | Person 1 |
| Constants & configuration | Person 1 |
| Resource loading (images/sounds) | Person 1 |
| Save/load game state | Person 1 |
| Map structure | Person 2 |
| Doors/traps/hazards | Person 2 |
| Alarm system | Person 2 |
| Drawing everything | Person 3 |
| Menus & screens | Person 3 |
| HUD (health bar, score) | Person 3 |
| Game loop | Person 4 |
| Input handling | Person 4 |
| Collision detection | Person 4 |
| Win/lose conditions | Person 4 |
| Loading levels | Person 4 |
| Main.java | Person 4 |
| Integration of all components | Person 4 |

---
