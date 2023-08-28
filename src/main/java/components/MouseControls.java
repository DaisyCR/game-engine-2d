package components;

import engine.GameObject;
import engine.MouseListener;
import engine.Window;
import org.joml.Vector2f;
import util.Constants;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;

    public void pickUpObject(GameObject go){
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void placeObject(){
        this.holdingObject = null;
    }

    @Override
    public void editorUpdate(float deltaTime){
        if( holdingObject != null ){
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();

            //Snap object position to grid
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Constants.GRID_WIDTH.getValue()) * Constants.GRID_WIDTH.getValue() + Constants.GRID_WIDTH.getValue() / 2.0f);
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Constants.GRID_HEIGHT.getValue()) * Constants.GRID_HEIGHT.getValue() + Constants.GRID_HEIGHT.getValue() / 2.0f);;


            if( MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) ){
                placeObject();
            }
        }
    }
}
