package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * This class is responsible for creating the night effect in the game,
 * which involves gradually changing the opacity of a
 * black rectangle to simulate night time.
 */
public class Night {
    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final String NIGHT_TAG = "night";
    private static final float INITIAL_OPACITY = 0f;

    /**
     * Creates a GameObject representing the night effect.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The duration of the day-night cycle.
     * @return A GameObject representing the night.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.black));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);

        // Transition to change the opacity of the night
        // object to simulate night and day cycle.
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                INITIAL_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        return night;
    }
}