package components;

import editor.PropertiesWindow;
import engine.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Constants;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component{
    Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
    Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
    Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    Vector4f yAxisColorHover = new Vector4f(0,1,0,1);
    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6 / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);
    private float gizmoWidth = 16f / 80f;
    private float gizmoHeight = 48f/ 80f;
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;
    private boolean isUsing = false;


    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private PropertiesWindow propertiesWindow;
    protected GameObject activeGameobject = null;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        Window.getScene().addGameObjectToScene(xAxisObject);
        Window.getScene().addGameObjectToScene(yAxisObject);

    }

    @Override
    public void start(){
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float deltaTime){
        if(isUsing){
            this.setInactive();
        }
    }

    @Override
    public void editorUpdate(float deltaTime){
        if(!isUsing) return;

        this.activeGameobject = this.propertiesWindow.getActiveGameObject();
        //TODO move this to its own class
        if (this.activeGameobject != null) {
            this.setActive();
            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)){
                GameObject newObj = this.activeGameobject.copy();
                Window.getScene().addGameObjectToScene(newObj);
                newObj.transform.position.add(0.1f, 0.1f);
                this.propertiesWindow.setActiveGameObject(newObj);
                return;
            } else if((KeyListener.keyBeginPress(GLFW_KEY_DELETE))){
                activeGameobject.destroy();
                this.setInactive();
                this.propertiesWindow.setActiveGameObject(null);
                return;
            }
        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if(this.activeGameobject != null){
            this.xAxisObject.transform.position.set(this.activeGameobject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.set(this.activeGameobject.transform.position);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    public GameObject getActiveGameobject(GameObject go){
        return this.activeGameobject;
    }

    public void setActive(){
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    public void setInactive(){
        this.activeGameobject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if(mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f)&&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)){
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if(mousePos.x <= yAxisObject.transform.position.x + (gizmoHeight / 2.0f)&&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f)&&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoWidth / 2.0f)){
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing(){
        this.isUsing = true;
    }

    public void setNotUsing(){
        this.isUsing = false;
        this.setInactive();
    }
}
