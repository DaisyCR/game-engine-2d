package editor;

import components.NonPickable;
import engine.GameObject;
import engine.MouseListener;
import imgui.ImGui;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;
    private float deBounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime, Scene currentScene){
        deBounce -= deltaTime;
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && deBounce < 0){
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);

            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if(pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                activeGameObject = pickedObj;
            } else if(pickedObj == null && !MouseListener.isDragging()){
                activeGameObject = null;
            }
            this.deBounce = 0.2f;
        }
    }

    public void imGui() {
        if (activeGameObject != null) {
            ImGui.begin("Object Properties");
            activeGameObject.imGui();
            ImGui.end();
        }

    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }
}
