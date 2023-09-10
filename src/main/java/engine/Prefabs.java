package engine;

import components.*;
import util.AssetPool;
import util.Settings;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY){
        GameObject spriteObject = Window.getScene().createGameObject("New Object");
        spriteObject.transform.scale.x = sizeX;
        spriteObject.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        spriteObject.addComponent(renderer);

        return spriteObject;
    }

    public static GameObject generatePlayer() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/characters.png");
        GameObject player = generateSpriteObject(playerSprites.getSprite(0), Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        player.addComponent(stateMachine);

        return player;
    }
}
