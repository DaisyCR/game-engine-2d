package editor;

import components.NonPickable;
import engine.GameObject;
import engine.MouseListener;
import imgui.ImGui;
import physics2d.components.BoxCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private List<GameObject> activeGameObjectsList = new ArrayList<>();
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void imGui() {
        if (activeGameObjectsList.size() == 1 && activeGameObjectsList.get(0) != null) {
            activeGameObject = activeGameObjectsList.get(0);
            ImGui.begin("Object Properties");

            if(ImGui.beginPopupContextWindow("Add Component")){
                if(ImGui.menuItem("Add Rigidbody")){
                    if(activeGameObject.getComponent(RigidBody2D.class) == null){
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }
                if(ImGui.menuItem("Add BoxCollider")){
                    if(activeGameObject.getComponent(BoxCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null){
                        activeGameObject.addComponent(new BoxCollider());
                    }
                }
                if(ImGui.menuItem("Add CircleCollider")){
                    if(activeGameObject.getComponent(CircleCollider.class) == null && activeGameObject.getComponent(BoxCollider.class) == null){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }
                ImGui.endPopup();
            }

            activeGameObject.imGui();
            ImGui.end();
        }

    }

    public GameObject getActiveGameObject() {
        return activeGameObjectsList.size() == 1 ? this.activeGameObjectsList.get(0) : null;
    }

    public List<GameObject> getActiveGameObjectsList() {
        return this.activeGameObjectsList;
    }

    public void clearSelected() {
        this.activeGameObjectsList.clear();
    }

    public void addActiveGameObject(GameObject go) {
        this.activeGameObjectsList.add(go);
    }

    public void setActiveGameObject(GameObject go) {
        if(go != null){
            clearSelected();
            this.activeGameObjectsList.add(go);
        }
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
