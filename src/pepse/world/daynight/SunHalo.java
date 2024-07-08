package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;

import java.awt.*;

/**
 * This class is responsible for creating a halo effect around the sun.
 */
public class SunHalo {
    private static final String SUN_HALO_TAG = "sunHalo";
    private static final float HALO_RADIUS = 1.5f;
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);

    /**
     * Creates a GameObject representing the sun's halo.
     *
     * @param sun The sun GameObject around which the halo is created.
     * @return A GameObject representing the sun's halo.
     */
    public static GameObject create(GameObject sun) {
        GameObject sunHalo = new GameObject(
                sun.getTopLeftCorner(),
                sun.getDimensions().mult(HALO_RADIUS),
                new OvalRenderable(HALO_COLOR)
        );
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);

        // Component to keep the halo centered around the sun
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}

