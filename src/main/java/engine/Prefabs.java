package engine;

import components.Sprite;
import components.SpriteRenderer;
import org.joml.Vector2f;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject spriteObject = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        spriteObject.addComponent(renderer);

        return spriteObject;
    }
}
