package components;

import engine.GameObject;
import engine.KeyListener;
import engine.MouseListener;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Constants;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    public void pickUpObject(GameObject go){
        if(this.holdingObject != null){
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public void placeObject(){
        GameObject copyObject = this.holdingObject.copy();
        copyObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        copyObject.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(copyObject);
        this.holdingObject = null;
    }

    @Override
    public void editorUpdate(float deltaTime){
        debounce -= deltaTime;
        if( holdingObject != null && debounce <= 0 ){
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();

            //Snap object position to grid
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Constants.GRID_WIDTH.getValue()) * Constants.GRID_WIDTH.getValue() + Constants.GRID_WIDTH.getValue() / 2.0f);
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Constants.GRID_HEIGHT.getValue()) * Constants.GRID_HEIGHT.getValue() + Constants.GRID_HEIGHT.getValue() / 2.0f);;


            if( MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) ){
                placeObject();
                debounce = debounceTime;
            }

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }
}
