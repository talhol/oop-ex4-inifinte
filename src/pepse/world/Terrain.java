package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.BlockUtil;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * The Terrain class is responsible for generating and
 * managing the terrain within the game world.
 */
public class Terrain {
    private float groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 30; // The depth of the terrain in blocks
    private static final String GROUND_TAG = "ground"; // Tag for ground blocks
    private static final float GROUND_HEIGHT_RATIO = 2.0f / 3.0f; // Ratio to calculate initial ground height
    private static final int NOISE_SCALE_FACTOR = 7; // Scale factor for noise generation
    private static int seed;

    /**
     * Constructor for the Terrain class.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed The seed for noise generation to ensure consistent terrain.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT_RATIO;
        Terrain.seed = seed;
    }

    /**
     * Returns the ground height at a given x-coordinate.
     *
     * @param x The x-coordinate.
     * @return The ground height at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        NoiseGenerator noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * NOISE_SCALE_FACTOR);
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates terrain blocks within the specified range.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of blocks created within the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        int finalMinX = BlockUtil.getNearestBlockLocation(minX);
        int finalMaxX = BlockUtil.getNearestBlockLocation(maxX);
        int currentX = finalMinX;
        List<Block> blocks = new LinkedList<>();

        while (currentX < finalMaxX) {
            float y = (float) (Math.floor(groundHeightAt(currentX) / Block.SIZE) * Block.SIZE);
            addBlocksAtX(currentX, y, blocks);
            currentX += Block.SIZE;
        }

        return blocks;
    }

    /**
     * Adds blocks at a specified x-coordinate, starting from a given y-coordinate, and going downwards.
     *
     * @param currentX The x-coordinate.
     * @param y The starting y-coordinate.
     * @param blocks The list of blocks to which new blocks will be added.
     */
    private static void addBlocksAtX(int currentX, float y, List<Block> blocks) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Vector2 blockPosition = Vector2.of(currentX, y);
            RectangleRenderable renderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            Block block = new Block(blockPosition, renderable);
            block.setTag(GROUND_TAG);
            blocks.add(block);
            y += Block.SIZE;
        }
    }
}
