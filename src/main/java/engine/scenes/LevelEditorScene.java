package engine.scenes;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import engine.Camera;
import engine.GameObject;
import engine.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    public LevelEditorScene(){

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f());

        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        SpriteRenderer obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(1,1,1,1));
        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 0);
        obj1.addComponent(obj1Sprite);
        this.addGameObjectToScene(obj1);

        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/testImage.png"));
        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 0);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);

        this.activeGameObject = obj1;
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet( AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
    }

    @Override
    public void update(float deltaTime) {
        System.out.println("FPS: " + (1 / deltaTime));
        for(GameObject go : this.gameObjects){
            go.update(deltaTime);
        }
        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Test Window");
        ImGui.text("Hi, i'm testing this window");
        ImGui.end();
    }
}
