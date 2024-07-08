package pepse.world.trees;

import danogl.util.Vector2;
import pepse.util.BlockUtil;
import pepse.world.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is responsible for generating and
 * managing trees within a specified range.
 */
public class Flora {
    private static final double TREE_PROBABILITY = 0.15;
    private final Function<Float, Float> groundHeightAtX;
    private final int seed;

    /**
     * Constructor for the Flora class.
     *
     * @param groundHeightAtX Function to get the
     *                        ground height at a given x-coordinate.
     */
    public Flora(Function<Float, Float> groundHeightAtX, int seed) {
        this.groundHeightAtX = groundHeightAtX;
        this.seed = seed;
    }

    /**
     * Creates trees within the specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of trees created within the range.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        LinkedList<Tree> trees = new LinkedList<>();
        int finalMinX = BlockUtil.getNearestBlockLocation(minX);
        int finalMaxX = BlockUtil.getNearestBlockLocation(maxX);
        int currentX = finalMinX;

        while (currentX < finalMaxX) {
            int hash = Objects.hash(currentX, seed);
            Random random = new Random(hash); // seeded random
            float groundHeightAtX = this.groundHeightAtX.apply((float) currentX);
            maybeAddTree(Vector2.of(currentX, groundHeightAtX), trees, random);
            currentX += Block.SIZE * 2;
        }

        return trees;
    }

    /**
     * Determines whether to add a tree at the given ground height.
     *
     * @param groundHeight The ground height at the current x-coordinate.
     * @param trees        The list of trees to which a new tree might be added.
     */
    private void maybeAddTree(Vector2 groundHeight, LinkedList<Tree> trees, Random random) {
        if (random.nextDouble() < TREE_PROBABILITY) {
            trees.add(new Tree(groundHeight, random));
        }
    }
}