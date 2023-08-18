package scenes;

import components.*;
import engine.Camera;
import engine.GameObject;
import engine.Prefabs;
import engine.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene{
    private Spritesheet sprites;
    private GameObject levelEditorObject = new GameObject("Level Editor Object", new Transform(new Vector2f()), 0);

    public LevelEditorScene(){

    }

    @Override
    public void init() {
        levelEditorObject.addComponent(new MouseControls());
        levelEditorObject.addComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if(levelLoaded){
            this.activeGameObject = gameObjects.get(0);
            return;
        }

/*
        SpriteRenderer obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(1,1,1,1));
        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)), 0);
        obj1.addComponent(new RigidBody());
        obj1.addComponent(obj1Sprite);
        this.addGameObjectToScene(obj1);

        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/testImage.png"));
        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 0);
        obj2.addComponent(obj2SpriteRenderer);
        obj2.addComponent(new RigidBody());
        this.addGameObjectToScene(obj2);
        */
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet( AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.getTexture("assets/images/testImage.png");
    }

    @Override
    public void update(float deltaTime) {
        levelEditorObject.update(deltaTime);
        //System.out.println("FPS: " + (1 / deltaTime));
        for(GameObject go : this.gameObjects){
            go.update(deltaTime);
        }
        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Sprites");

        ImVec2 windowPos = new ImVec2();
        ImVec2 windowSize = new ImVec2();
        ImVec2 itemSpacing = new ImVec2();

        ImGui.getWindowPos(windowPos);
        ImGui.getWindowSize(windowSize);
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for( int i = 0; i < sprites.size(); i++ ){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if( ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y) ){
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                levelEditorObject.getComponent(MouseControls.class).pickUpObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if( i + 1 < sprites.size() && nextButtonX2 < windowX2 ){
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
