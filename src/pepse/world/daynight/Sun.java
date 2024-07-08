package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * This class is responsible for creating the sun object and managing its movement in the sky.
 */
public class Sun {
    private static final String SUN_TAG = "sun";
    private static final float INITIAL_ANGLE = 0f;
    private static final float FINAL_ANGLE = 360f;
    private static final float SUN_Y_POSITION_RATIO = 9f;
    private static final float SUN_SIZE_RATIO = 10f;
    private static final float CYCLE_CENTER_Y_RATIO = 2f / 3f;
    private static final float INITIAL_SUN_CENTER_Y_RATIO = 0.5f;

    /**
     * Creates a GameObject representing the sun.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The duration of the day-night cycle.
     * @return A GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength, float sunCenterY) {
        // Position and size of the sun
        Vector2 sunPosition = Vector2.of(windowDimensions.x() / 2,
                windowDimensions.y() / SUN_Y_POSITION_RATIO);
        Vector2 sunSize = Vector2.of(windowDimensions.x() / SUN_SIZE_RATIO,
                windowDimensions.x() / SUN_SIZE_RATIO);

        GameObject sun = new GameObject(sunPosition, sunSize,
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        // Center of the sun's circular path
        Vector2 cycleCenter = Vector2.of(windowDimensions.x() / 2,
                sunCenterY);
        Vector2 initialSunCenter = new Vector2(cycleCenter.x(),
                cycleCenter.y() / 2);

        // Transition to move the sun in a circular path
        new Transition<>(
                sun,
                angle -> sun.setCenter(
                        initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)
                ),
                INITIAL_ANGLE,
                FINAL_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return sun;
    }
}