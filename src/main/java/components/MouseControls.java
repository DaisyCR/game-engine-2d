package components;

import engine.GameObject;
import engine.MouseListener;
import engine.Window;
import util.Constants;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{
    GameObject holdingObject = null;

    public void pickUpObject(GameObject go){
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void placeObject(){
        this.holdingObject = null;
    }

    @Override
    public void update(float deltaTime){
        if( holdingObject != null ){
            holdingObject.transform.position.x = MouseListener.getOrthoX();
            holdingObject.transform.position.y = MouseListener.getOrthoY();

            //Snap object position to grid
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Constants.GRID_WIDTH) * Constants.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Constants.GRID_HEIGHT) * Constants.GRID_HEIGHT;


            if( MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) ){
                placeObject();
            }
        }
    }
}
