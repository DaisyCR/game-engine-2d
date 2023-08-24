package components;

import editor.ImGuiTools;
import engine.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1,1,1,1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float deltaTime) {
        if( !this.lastTransform.equals(this.gameObject.transform) ){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float deltaTime) {
        if( !this.lastTransform.equals(this.gameObject.transform) ){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imGui(){
        if(ImGuiTools.colorPicker4Controls("Color picker", this.color)){
            this.isDirty = true;
        }
    }

    public Vector4f getColor(){
        return this.color;
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords(){
        return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color){
        if( !this.color.equals(color) ){
            this.color.set(color);
            this.isDirty = true;
        }
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }

    public boolean isDirty(){
        return this.isDirty;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void setClean(){
        this.isDirty = false;
    }
}
