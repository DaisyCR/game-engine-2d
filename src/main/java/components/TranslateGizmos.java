package components;

import editor.PropertiesWindow;
import engine.GameObject;
import engine.Prefabs;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TranslateGizmos extends Component{
    Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);
    Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);
    Vector4f xAxisColorHover = new Vector4f();
    Vector4f yAxisColorHover = new Vector4f();
    private Vector2f xAxisOffset = new Vector2f(64, 8);
    private Vector2f yAxisOffset = new Vector2f(25, 60);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private PropertiesWindow propertiesWindow;
    private GameObject activeGameobject = null;

    public TranslateGizmos(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
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
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float deltaTime){
        if(this.activeGameobject != null){
            this.xAxisObject.transform.position.set(this.activeGameobject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.set(this.activeGameobject.transform.position);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
        this.activeGameobject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameobject != null) {
            this.setActive();
        } else {
            this.setInactive();
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
}
