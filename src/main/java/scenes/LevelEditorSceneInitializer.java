package scenes;

import components.*;
import editor.Sound;
import editor.EditorCamera;
import editor.GridLines;
import components.MouseControls;
import engine.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;
import util.Constants;

import java.io.File;
import java.util.Collection;

public class LevelEditorSceneInitializer extends SceneInitializer{
    private GameObject levelEditorObject;
    private Spritesheet blocks;
    private Spritesheet gizmos;

    public LevelEditorSceneInitializer(){

    }

    @Override
    public void init(Scene scene) {
        gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");
        blocks = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        levelEditorObject = scene.createGameObject("Level Editor Object");
        levelEditorObject.addComponent(new MouseControls());
        levelEditorObject.addComponent(new KeyControls());
        levelEditorObject.addComponent(new GridLines());
        levelEditorObject.addComponent(new EditorCamera(scene.camera()));
        levelEditorObject.addComponent(new GizmoSystem(gizmos));
        levelEditorObject.setNoSerialize();
        scene.addGameObjectToScene(levelEditorObject);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getTexture("assets/images/testImage.png");
        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet( AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/characters.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/characters.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        for(GameObject go : scene.getGameObjects()){
            if(go.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if(spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            if(go.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = go.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imGui() {
        levelPropertiesWindowImGui();
        objectsWindowsImGui();
    }

    private void levelPropertiesWindowImGui() {
        ImGui.begin("Level Editor Properties");
        levelEditorObject.imGui();
        ImGui.end();
    }

    private void objectsWindowsImGui() {
        ImGui.begin("Sprites");

        if(ImGui.beginTabBar("Objects")) {
            if(ImGui.beginTabItem("Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImVec2 windowSize = new ImVec2();
                ImVec2 itemSpacing = new ImVec2();

                ImGui.getWindowPos(windowPos);
                ImGui.getWindowSize(windowSize);
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < blocks.size(); i++) {
                    Sprite sprite = blocks.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, Constants.GRID_WIDTH.getValue(), Constants.GRID_HEIGHT.getValue());
                        levelEditorObject.getComponent(MouseControls.class).pickUpObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

                    if (i + 1 < blocks.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Prefabs")){
                    Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/characters.png");
                    Sprite sprite = playerSprites.getSprite(0);
                    float spriteWidth = sprite.getWidth() * 2;
                    float spriteHeight = sprite.getHeight() * 2;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generatePlayer();
                        levelEditorObject.getComponent(MouseControls.class).pickUpObject(object);
                    }
                    ImGui.sameLine();

                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Sounds")){
                Collection<Sound> sounds = AssetPool.getAllSound();
                for(Sound sound : sounds){
                    File tmp = new File(sound.getFilepath());
                    if(ImGui.button(tmp.getName())){
                        if(!sound.isPlaying()){
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }
                    if(ImGui.getContentRegionAvailX() > 100){
                        ImGui.sameLine();
                    }

                }

                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
