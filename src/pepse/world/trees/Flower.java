package pepse.world.trees;

import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

import static pepse.util.ColorSupplier.approximateColor;

/**
 * This class represents a Flower, a type of flora game object.
 */
public class Flower extends FloraGameObject {
    private static final int FLOWER_SIZE = (int) (Block.SIZE * 0.8);
    private static final Color FLOWER_COLOR = new Color(37, 189, 19);
    private static final int FLOWER_MOVE_LENGTH = 2;
    private static final String FLOWER_TAG = "flower";
    private static final float MAX_DELAY = 2.5f;
    private static final float WIND_ANGLE = 10f;
    private static final float ANGLE_ON_JUMP = 90f;
    private static final float SIZE_MULTIPLIER = 1.1f;

    /**
     * Constructor for the Flower class.
     *
     * @param flowerTopLeft The top-left position of the flower.
     */
    public Flower(Vector2 flowerTopLeft) {
        super(flowerTopLeft.subtract(Vector2.DOWN.mult(FLOWER_SIZE)),
                Vector2.ONES.mult(FLOWER_SIZE),
                new RectangleRenderable(approximateColor(FLOWER_COLOR)));
        this.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        new ScheduledTask(this,
                new Random().nextFloat(0, MAX_DELAY),
                false, () -> {
            new Transition<>(this,
                    this.renderer()::setRenderableAngle,
                    -WIND_ANGLE,
                    WIND_ANGLE,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    FLOWER_MOVE_LENGTH,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
            new Transition<>(this,
                    this::setDimensions,
                    Vector2.ONES.mult(FLOWER_SIZE),
                    Vector2.ONES.mult(FLOWER_SIZE).mult(SIZE_MULTIPLIER),
                    Transition.CUBIC_INTERPOLATOR_VECTOR,
                    FLOWER_MOVE_LENGTH,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
        });
        this.setTag(FLOWER_TAG);
    }

    @Override
    public Runnable onJump() {
        return () -> {
            new Transition<>(this,
                    this.renderer()::setRenderableAngle,
                    0f, ANGLE_ON_JUMP,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    1,
                    Transition.TransitionType.TRANSITION_ONCE,
                    null);
        };
    }
}