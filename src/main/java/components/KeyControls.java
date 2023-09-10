package components;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import util.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component{
    @Override
    public void editorUpdate(float deltaTime){
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjectsList = propertiesWindow.getActiveGameObjectsList();

        if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null){
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0f);
            propertiesWindow.setActiveGameObject(newObj);
        } else if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjectsList.size() > 1){
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjectsList);
            propertiesWindow.clearSelected();
            for(GameObject go : gameObjects){
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }

        } else if((KeyListener.keyBeginPress(GLFW_KEY_DELETE))){
            for(GameObject go : activeGameObjectsList){
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}
