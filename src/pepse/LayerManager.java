package pepse;

import danogl.collisions.Layer;

/**
 * A manager class to handle different layers in the PEPSE game.
 */
public class LayerManager {

    /**
     * Enum representing different layers in the game.
     */
    public enum GameLayer {
        AVATAR(Layer.DEFAULT),
        TERRAIN(Layer.STATIC_OBJECTS),
        TREE(Layer.STATIC_OBJECTS),
        FRUIT(Layer.STATIC_OBJECTS + 2),
        FLOWER(Layer.STATIC_OBJECTS + 1),
        SKY(Layer.BACKGROUND),
        NIGHT(Layer.FOREGROUND),
        SUN(Layer.BACKGROUND),
        SUN_HALO(Layer.BACKGROUND),
        ENERGY(Layer.UI);

        private final int layer;

        GameLayer(int layer) {
            this.layer = layer;
        }

        /**
         * Gets the integer value of the layer.
         *
         * @return The integer value of the layer.
         */
        public int getLayer() {
            return layer;
        }
    }

    /**
     * Gets the layer for a specific game element.
     *
     * @param gameLayer The game layer enum.
     * @return The integer value of the layer.
     */
    public static int getLayer(GameLayer gameLayer) {
        return gameLayer.getLayer();
    }
}
