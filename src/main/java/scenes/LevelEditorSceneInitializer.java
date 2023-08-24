package scenes;

import components.*;
import editor.EditorCamera;
import editor.GridLines;
import components.MouseControls;
import engine.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;
import util.Constants;

public class LevelEditorSceneInitializer extends SceneInitializer{
    private GameObject levelEditorObject;
    private Spritesheet sprites;
    private Spritesheet gizmos;

    public LevelEditorSceneInitializer(){

    }

    @Override
    public void init(Scene scene) {
        gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        levelEditorObject = scene.createGameObject("Level Editor Object");
        levelEditorObject.addComponent(new MouseControls());
        levelEditorObject.addComponent(new GridLines());
        levelEditorObject.addComponent(new EditorCamera(scene.camera()));
        levelEditorObject.addComponent(new GizmoSystem(gizmos));
        levelEditorObject.setNoSerialize();
        scene.addGameObjectToScene(levelEditorObject);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png", new Spritesheet( AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/gizmos.png", new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"), 24, 48, 3, 0));
        AssetPool.getTexture("assets/images/testImage.png");

        for(GameObject go : scene.getGameObjects()){
            if(go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if(spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
    public void imGui() {
        levelPropertiesWindowImGui();
        spritesWindowImGui();
    }

    private void levelPropertiesWindowImGui() {
        ImGui.begin("Level Editor Properties");
        levelEditorObject.imGui();
        ImGui.end();
    }

    private void spritesWindowImGui() {
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
                GameObject object = Prefabs.generateSpriteObject(sprite, Constants.GRID_WIDTH.getValue(), Constants.GRID_HEIGHT.getValue());
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
