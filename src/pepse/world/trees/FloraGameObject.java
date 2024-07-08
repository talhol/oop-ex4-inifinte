package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Abstract class representing a flora game object.
 */
public abstract class FloraGameObject extends GameObject {

    /**
     * Construct a new FloraGameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param size          Size of the object.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public FloraGameObject(Vector2 topLeftCorner, Vector2 size, Renderable renderable) {
        super(topLeftCorner, size, renderable);
    }

    /**
     * Abstract method to be implemented by subclasses to define behavior on jump.
     *
     * @return A Runnable to execute on jump.
     */
    public abstract Runnable onJump();
}
