package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * The Energy class represents the energy bar in the game.
 */
public class Energy extends GameObject {
    // Constants for the size of the energy bar and initial position
    private static final int SIZE = 30;
    private static final Vector2 INITIAL_POSITION =
            Vector2.ONES.multX(30).multY(20);
    private static final String ENERGY_TEXT_PREFIX = "Energy: ";
    private final Supplier<Double> energyCallback;

    /**
     * Constructor for the Energy class.
     *
     * @param energyCallback A callback function to get the current energy level.
     */
    public Energy(Supplier<Double> energyCallback) {
        super(INITIAL_POSITION, Vector2.ONES.mult(SIZE),
                new TextRenderable(ENERGY_TEXT_PREFIX + energyCallback.get()));
        this.energyCallback = energyCallback;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        TextRenderable renderable = (TextRenderable) this.renderer().getRenderable();
        renderable.setString(ENERGY_TEXT_PREFIX + energyCallback.get());
    }
}
