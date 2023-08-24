package engine;

import components.Sprite;
import components.SpriteRenderer;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject spriteObject = Window.getScene().createGameObject("Sprite_Object_Gen");
        spriteObject.transform.scale.x = sizeX;
        spriteObject.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        spriteObject.addComponent(renderer);

        return spriteObject;
    }
}
