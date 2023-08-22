package editor;

import components.Component;
import engine.Camera;
import engine.KeyListener;
import engine.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {
    private float dragDebounce = 0.05f; //TODO FIX MOUSEDRAGGING FUNCTION

    private final Camera levelEditorCamera;
    private Vector2f clickOrigin;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 60.0f;
    private float scrollSensitivity = 0.1f;
    private float cameraSpeed = 0.5f;
    private boolean reset = false;

    public EditorCamera(Camera LevelEditorCamera) {
        this.levelEditorCamera = LevelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float deltaTime){
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0){
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= deltaTime;
            return;
        } else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f mouseDistance = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(mouseDistance.mul(deltaTime).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, deltaTime);
        }

        if(dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            dragDebounce = 0.05f;
        }

        //TODO fix bug where snap to grid breaks when camera moves
        //TODO add zoom option with keyboard

        if(KeyListener.isKeyPressed(GLFW_KEY_KP_0)){
            reset = true;
        }

        if(reset){
            levelEditorCamera.position.lerp(new Vector2f(0,0), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += cameraSpeed * deltaTime;
            if(Math.abs(levelEditorCamera.position.x) <= 5.0f && Math.abs(levelEditorCamera.position.x) <= 5.0f){
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                this.lerpTime = 0.0f;
                reset = false;
            }
        }
    }
}
