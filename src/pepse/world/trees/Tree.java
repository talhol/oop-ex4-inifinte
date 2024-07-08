package pepse.world.trees;

import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static pepse.util.ColorSupplier.approximateColor;

/**
 * This class represents a Tree, a type of flora game object.
 */
public class Tree extends FloraGameObject {
    private static final int TREE_HEIGHT_BASE = Block.SIZE * 8;
    private static final float TREE_HEIGHT_MIN_MULTIPLIER = 0.6f;
    private static final float TREE_HEIGHT_MAX_MULTIPLIER = 1.2f;
    private static final int MIN_FRUITS = 3;
    private static final int MAX_FRUITS = 6;
    private static final int BOUND_AROUND_TOP_TREE = 3;
    private static final Color TREE_BLOCK_COLOR = new Color(100, 50, 20);
    private static final String TREE_TAG = "tree";
    public static final float PROBABILITY_TO_CREATE_FLOWER = 0.7f;
    private final List<Flower> flowers;
    private final List<Fruit> fruits;
    private Random random;

    /**
     * Constructor for the Tree class.
     *
     * @param groundHeight The ground height at the base of the tree.
     */
    public Tree(Vector2 groundHeight, Random random) {
        float treeHeight = TREE_HEIGHT_BASE *
                random.nextFloat(TREE_HEIGHT_MIN_MULTIPLIER, TREE_HEIGHT_MAX_MULTIPLIER);
        float treeWidth = Block.SIZE;
        Vector2 treeBlockSize = Vector2.ONES.multY(treeHeight).multX(treeWidth);
        super(groundHeight.subtract(Vector2.DOWN.mult(treeHeight)), treeBlockSize,
                new RectangleRenderable(approximateColor(TREE_BLOCK_COLOR)));

        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        this.random = random;
        this.flowers = addFlowerAroundTreeTop(this.getTopLeftCorner());
        this.fruits = addFruitsAroundTreeTop(this.getTopLeftCorner());

        this.setTag(TREE_TAG);
    }

    /**
     * Returns the list of flowers around the tree.
     *
     * @return List of Flower objects
     */
    public List<Flower> getFlowers() {
        return flowers;
    }

    /**
     * Returns the list of fruits around the tree.
     *
     * @return List of Fruit objects
     */
    public List<Fruit> getFruits() {
        return this.fruits;
    }

    /**
     * Generates a random position around the tree top.
     *
     * @param treeCenter The center position of the tree.
     * @return A random position around the tree top.
     */
    private Vector2 getRandomPositionAroundTree(Vector2 treeCenter, Random random) {
        return treeCenter.add(Vector2.ONES.mult(Block.SIZE)
                .multX(random.nextInt(-BOUND_AROUND_TOP_TREE, BOUND_AROUND_TOP_TREE))
                .multY(random.nextInt(-BOUND_AROUND_TOP_TREE, BOUND_AROUND_TOP_TREE)));
    }


    /**
     * Adds flowers around the top of the tree.
     *
     * @param treeCenter The center position of the tree.
     * @return A list of Flower objects.
     */
    private List<Flower> addFlowerAroundTreeTop(Vector2 treeCenter) {
        List<Flower> flowers = new LinkedList<>();
        int numRowsOfFlowers = BOUND_AROUND_TOP_TREE * 2;
        int numColsOfFlowers = BOUND_AROUND_TOP_TREE * 2;

        for (int i = 0; i < numRowsOfFlowers; i++) {
            for (int j = 0; j < numColsOfFlowers; j++) {
                if (random.nextFloat(0, 1f) < PROBABILITY_TO_CREATE_FLOWER) {
                    Vector2 topLeft = treeCenter.add(Vector2.ONES.mult(Block.SIZE)
                            .multX(i - BOUND_AROUND_TOP_TREE)
                            .multY(j - BOUND_AROUND_TOP_TREE));
                    flowers.add(new Flower(topLeft));
                }
            }
        }

        return flowers;
    }

    /**
     * Adds fruits around the top of the tree.
     *
     * @param treeCenter The center position of the tree.
     * @return A list of Fruit objects.
     */
    private List<Fruit> addFruitsAroundTreeTop(Vector2 treeCenter) {
        List<Fruit> fruits = new LinkedList<>();
        int numFruits = random.nextInt(MIN_FRUITS, MAX_FRUITS);
        for (int i = 0; i < numFruits; i++) {
            Vector2 topLeft = getRandomPositionAroundTree(treeCenter, random);
            fruits.add(new Fruit(topLeft));
        }
        return fruits;
    }

    /**
     * Returns all tree elements including the tree, flowers, and fruits.
     *
     * @return A list of FloraGameObject objects.
     */
    public List<FloraGameObject> getAllTreeElements() {
        List<FloraGameObject> floraGameObjects = new LinkedList<>();
        floraGameObjects.addAll(flowers);
        floraGameObjects.addAll(fruits);
        floraGameObjects.add(this);
        return floraGameObjects;
    }

    @Override
    public Runnable onJump() {
        return () -> {
            this.renderer().setRenderable(
                    new RectangleRenderable(approximateColor(TREE_BLOCK_COLOR)));
        };
    }
}