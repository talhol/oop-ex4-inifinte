package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This class represents a Fruit, a type of flora game object.
 */
public class Fruit extends FloraGameObject {
    private static final int FRUIT_SIZE = 25;
    private static final Color FRUIT_COLOR = new Color(107, 19, 189);
    private static final String FRUIT_TAG = "fruit";
    private Consumer<GameObject> collisionCallback;
    private static final int RANDOM_COLOR_BOUND = 256;

    /**
     * Constructor for the Fruit class.
     *
     * @param fruitTopLeft The top-left position of the fruit.
     */
    public Fruit(Vector2 fruitTopLeft) {
        super(fruitTopLeft.subtract(Vector2.DOWN.mult(FRUIT_SIZE)),
                Vector2.ONES.mult(FRUIT_SIZE),
                new OvalRenderable(createRandomFruitColor()));
        this.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        this.setTag(FRUIT_TAG);
    }

    /**
     * Sets the collision callback to be executed
     * when the fruit collides with another object.
     *
     * @param collisionCallback The collision callback.
     */
    public void setCollisionCallback(Consumer<GameObject> collisionCallback) {
        this.collisionCallback = collisionCallback;
    }

    private static Color createRandomFruitColor() {
        Random random = new Random();
        return new Color(random.nextInt(RANDOM_COLOR_BOUND),
                random.nextInt(RANDOM_COLOR_BOUND),
                random.nextInt(RANDOM_COLOR_BOUND));
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (collisionCallback != null) {
            collisionCallback.accept(other);
        }
    }

    @Override
    public Runnable onJump() {
        Random random = new Random();
        return () -> {
            this.renderer().setRenderable(new OvalRenderable(
                    createRandomFruitColor()));
        };
    }
}
