package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.util.BlockUtil;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.*;
import danogl.gui.rendering.Camera;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static pepse.util.BlockUtil.getNearestBlockLocation;
import static pepse.world.Avatar.AVATAR_HEIGHT;

/**
 * The main game manager for the PEPSE game.
 */
public class PepseGameManager extends GameManager {

    private final int CYCLE_LENGTH = 30;
    private final float EATEN_FRUIT_ENERGY = 10f;
    private static final String AVATAR_TAG = "avatar";
    private static final int SEED = 12;
    private int minX;
    private int maxX;
    private int WORLD_BUFFER = Block.SIZE * 10;
    private Vector2 windowDimensions;
    private Terrain terrain;
    private Avatar avatar;

    /**
     * Initializes the game and sets up the game objects.
     *
     * @param imageReader      Responsible for reading images.
     * @param soundReader      Responsible for reading sounds.
     * @param inputListener    Listens to user input.
     * @param windowController Controls the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDimensions = windowController.getWindowDimensions();
        minX = -WORLD_BUFFER;
        maxX = (int) windowDimensions.x() + WORLD_BUFFER;
        GameObjectCollection gameObjects = gameObjects();
        gameObjects().layers().shouldLayersCollide(
                LayerManager.getLayer(LayerManager.GameLayer.AVATAR),
                LayerManager.getLayer(LayerManager.GameLayer.FRUIT),
                true);
        gameObjects().layers().shouldLayersCollide(
                LayerManager.getLayer(LayerManager.GameLayer.AVATAR),
                LayerManager.getLayer(LayerManager.GameLayer.TERRAIN),
                true);

        createSky(gameObjects, windowDimensions);
        terrain = new Terrain(windowDimensions, SEED);
        createDayNightCycle(gameObjects, windowDimensions);
        createAvatar(gameObjects, inputListener, imageReader, windowController);
        createWorldInRange(minX, maxX);
    }

    /**
     * Creates the sky and adds it to the game objects.
     *
     * @param gameObjects      The collection of game objects.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createSky(GameObjectCollection gameObjects, Vector2 windowDimensions) {
        gameObjects.addGameObject(Sky.create(windowDimensions),
                LayerManager.getLayer(LayerManager.GameLayer.SKY));
    }

    /**
     * Creates the terrain and adds it to the game objects.
     *
     * @param gameObjects The collection of game objects.
     * @return The created Terrain object.
     */
    private void createTerrain(GameObjectCollection gameObjects, int minX, int maxX) {
        List<Block> blocks = terrain.createInRange(minX, maxX);
        blocks.forEach(block -> gameObjects.addGameObject(block,
                LayerManager.getLayer(LayerManager.GameLayer.TERRAIN)));
    }

    /**
     * Creates the day-night cycle and adds the sun, night, and sun halo to the game objects.
     *
     * @param gameObjects      The collection of game objects.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createDayNightCycle(GameObjectCollection gameObjects, Vector2 windowDimensions) {
        float groundHeightAtMiddle = terrain.groundHeightAt(windowDimensions.x() / 2);
        GameObject sun = Sun.create(windowDimensions, CYCLE_LENGTH, groundHeightAtMiddle);
        gameObjects.addGameObject(sun,
                LayerManager.getLayer(LayerManager.GameLayer.SUN));
        gameObjects.addGameObject(Night.create(windowDimensions, (float) CYCLE_LENGTH / 2),
                LayerManager.getLayer(LayerManager.GameLayer.NIGHT));
        gameObjects.addGameObject(SunHalo.create(sun),
                LayerManager.getLayer(LayerManager.GameLayer.SUN_HALO));
    }

    /**
     * Creates the avatar and adds it to the game objects.
     *
     * @param gameObjects   The collection of game objects.
     * @param inputListener Listens to user input.
     * @param imageReader   Responsible for reading images.
     * @return The created Avatar object.
     */
    private void createAvatar(GameObjectCollection gameObjects, UserInputListener inputListener,
                              ImageReader imageReader, WindowController windowController) {
        float groundHeightAt0 = terrain.groundHeightAt(0f);
        Vector2 initialAvatarLocation = new Vector2(windowController.getWindowDimensions().x() / 2,
                groundHeightAt0 - AVATAR_HEIGHT * 2);
        avatar = new Avatar(initialAvatarLocation.subtract(Vector2.of(AVATAR_HEIGHT, AVATAR_HEIGHT).mult(0.5f)),
                inputListener, imageReader);
        gameObjects.addGameObject(avatar, LayerManager.getLayer(LayerManager.GameLayer.AVATAR));
        Energy energy = new Energy(avatar::getEnergy);
        gameObjects.addGameObject(energy, LayerManager.getLayer(LayerManager.GameLayer.ENERGY));
        Camera camera = new Camera(avatar,
                Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);
    }

    /**
     * Handles the collision between the avatar and a fruit.
     *
     * @param fruit The fruit object.
     */
    private void handleFruitCollision(Fruit fruit) {

        if (avatar.getTag().equals(AVATAR_TAG)) {
            avatar.addEnergy(EATEN_FRUIT_ENERGY);
            gameObjects().removeGameObject(fruit,
                    LayerManager.getLayer(LayerManager.GameLayer.FRUIT));
            new ScheduledTask(
                    avatar,
                    CYCLE_LENGTH,
                    false,
                    () -> gameObjects().addGameObject(fruit,
                            LayerManager.getLayer(LayerManager.GameLayer.FRUIT))
            );
        }
    }

    /**
     * Creates trees and adds them to the game objects.
     *
     * @param gameObjects The collection of game objects.
     */
    private void createTrees(GameObjectCollection gameObjects, int leftBound, int rightBound) {
        List<Tree> trees = new Flora(terrain::groundHeightAt, SEED)
                .createInRange(leftBound, rightBound);
        List<FloraGameObject> floraGameObjects = new LinkedList<>();

        trees.forEach(tree -> {
            floraGameObjects.addAll(tree.getAllTreeElements());
            gameObjects.addGameObject(tree,
                    LayerManager.getLayer(LayerManager.GameLayer.TREE));
            tree.getFlowers().forEach(flower -> gameObjects.addGameObject(flower,
                    LayerManager.getLayer(LayerManager.GameLayer.FLOWER)));
            tree.getFruits().forEach(fruit -> {
                gameObjects.addGameObject(fruit,
                        LayerManager.getLayer(LayerManager.GameLayer.FRUIT));
                fruit.setCollisionCallback((other) -> handleFruitCollision(fruit));
            });
        });

        avatar.addOnJump(floraGameObjects.stream().map(FloraGameObject::onJump).toList());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        generateInfiniteWorld();
    }

    /**
     * Generates new terrain and trees depending on location of avatar
     */
    private void generateInfiniteWorld() {
        float cameraMaxX = camera().screenToWorldCoords(windowDimensions).x();
        float cameraMinX = camera().screenToWorldCoords(windowDimensions).x() - windowDimensions.x();
        // right
        if (cameraMaxX >= maxX) {
            createWorldInRange(maxX, maxX + WORLD_BUFFER);
            maxX += WORLD_BUFFER;
            minX += WORLD_BUFFER;
            //left
        } else if (cameraMinX <= minX) {
            createWorldInRange(minX - WORLD_BUFFER, minX);
            maxX -= WORLD_BUFFER;
            minX -= WORLD_BUFFER;
        }
    }

    /**
     * Creates the world in the given range.
     */
    private void createWorldInRange(int leftX, int rightX) {
        int normaliedminX = BlockUtil.getNearestBlockLocation(leftX);
        int normailzedMaxX = BlockUtil.getNearestBlockLocation(rightX);
        createTerrain(gameObjects(), normaliedminX, normailzedMaxX);
        createTrees(gameObjects(), normaliedminX, normailzedMaxX);
        removeHiddenObjects();
    }

    /**
     * Removes all hidden objects.
     */
    private void removeHiddenObjects() {
        for (GameObject obj : gameObjects()) {
            if (obj.getCenter().x() < minX - WORLD_BUFFER || obj.getCenter().x() > maxX + WORLD_BUFFER) {
                removeHiddenGameObject(obj);
            }
        }
    }

    /**
     * Removes the game object and find the layer based on his type.
     */
    private void removeHiddenGameObject(GameObject object) {
        if (object instanceof Fruit) {
            gameObjects().removeGameObject(object, LayerManager.getLayer(LayerManager.GameLayer.FRUIT));
        }
        if (object instanceof Tree) {
            gameObjects().removeGameObject(object, LayerManager.getLayer(LayerManager.GameLayer.TREE));
        }
        if (object instanceof Flower) {
            gameObjects().removeGameObject(object, LayerManager.getLayer(LayerManager.GameLayer.FLOWER));
        }
        if (object instanceof Block) {
            gameObjects().removeGameObject(object, LayerManager.getLayer(LayerManager.GameLayer.TERRAIN));
        }
    }

    /**
     * The main method to run the game.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}