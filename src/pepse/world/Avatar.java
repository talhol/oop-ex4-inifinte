package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

/**
 * The Avatar class represents the player's character in the game.
 */
public class Avatar extends GameObject {
    // Constants for movement and animation
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static final String[] IDLE_IMAGES = {"assets/idle_0.png",
            "assets/idle_1.png", "assets/idle_2.png", "assets/idle_3.png"};
    private static final String[] RUN_IMAGES = {"assets/run_0.png",
            "assets/run_1.png", "assets/run_2.png", "assets/run_3.png",
            "assets/run_4.png", "assets/run_5.png"};
    private static final String[] JUMP_IMAGES = {"assets/jump_0.png",
            "assets/jump_1.png", "assets/jump_2.png", "assets/jump_3.png"};
    public static final int AVATAR_HEIGHT = 50;
    private static final int FULL_ENERGY = 1000000;
    private static final double STEP_ENERGY = 0.5;
    private static final double JUMP_ENERGY = 10;
    private static final String AVATAR_TAG = "avatar";
    private static final float ANIMATION_SPEED = 0.2f;

    // Avatar state and energy
    private AvatarState avatarState = AvatarState.IDLE;
    private List<Runnable> onJump = new LinkedList<>();
    private double energy = FULL_ENERGY;

    // Input listener and image reader
    private final UserInputListener inputListener;
    private final ImageReader imageReader;

    /**
     * Constructor for the Avatar class.
     *
     * @param topLeftCorner The initial position of the avatar.
     * @param inputListener The listener for user input.
     * @param imageReader   The reader for loading images.
     */
    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader) {
        super(topLeftCorner, Vector2.ONES.mult(AVATAR_HEIGHT),
                new ImageRenderable(imageReader.readImage(IDLE_IMAGES[0],
                        false).getImage()));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.setTag(AVATAR_TAG);
    }

    /**
     * Gets the current energy level of the avatar.
     *
     * @return The current energy level.
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * Adds the list of actions to perform on jump.
     *
     * @param onJump A list of actions to perform on jump.
     */
    public void addOnJump(List<Runnable> onJump) {
        this.onJump.addAll(onJump);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        // Handle left movement
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= handleMove();
        }

        // Handle right movement
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += handleMove();
        }

        transform().setVelocityX(xVel);
        renderer().setIsFlippedHorizontally(xVel < 0);

        // Handle jumping
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            handleJump();
        }

        // Handle idle state
        if (getVelocity().y() == 0 && getVelocity().x() == 0) {
            handleIdle();
        }
    }

    /**
     * Adds energy to the avatar.
     *
     * @param energy The amount of energy to add.
     */
    public void addEnergy(double energy) {
        this.energy = min(FULL_ENERGY, this.energy + energy);
    }

    /**
     * Handles the movement of the avatar.
     *
     * @return The horizontal velocity.
     */
    private float handleMove() {
        if (energy < STEP_ENERGY) return 0;
        energy -= STEP_ENERGY;
        if (avatarState == AvatarState.IDLE) {
            avatarState = AvatarState.RUNNING;
            setAnimation(RUN_IMAGES);
        }
        return VELOCITY_X;
    }

    /**
     * Handles the jump action of the avatar.
     */
    private void handleJump() {
        if (energy < JUMP_ENERGY) return;
        avatarState = AvatarState.JUMPING;
        setAnimation(JUMP_IMAGES);
        onJump.forEach(Runnable::run);
        energy -= JUMP_ENERGY;
        transform().setVelocityY(VELOCITY_Y);
    }

    /**
     * Handles the idle state of the avatar.
     */
    private void handleIdle() {
        avatarState = AvatarState.IDLE;
        setAnimation(IDLE_IMAGES);
        if (energy < FULL_ENERGY) {
            energy += 1;
            if (energy > FULL_ENERGY) {
                energy = FULL_ENERGY;
            }
        }
    }

    /**
     * Sets the animation for the avatar.
     *
     * @param images The images to use for the animation.
     */
    private void setAnimation(String[] images) {
        this.renderer().setRenderable(new AnimationRenderable(images,
                imageReader,
                false,
                ANIMATION_SPEED));
    }

    /**
     * Enum representing the different states of the avatar.
     */
    public enum AvatarState {
        RUNNING,
        IDLE,
        JUMPING
    }
}
