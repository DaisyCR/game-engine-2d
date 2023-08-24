package components;

import engine.KeyListener;
import engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmosSprites) {
        gizmos = gizmosSprites;
    }

    @Override
    public void start(){
        gameObject.addComponent(new TranslateGizmos(gizmos.getSprite(1), Window.get().getImGuiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.get().getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void editorUpdate(float deltaTime){
        if(usingGizmo == 0){
            gameObject.getComponent(TranslateGizmos.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if(usingGizmo == 1){
            gameObject.getComponent(TranslateGizmos.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_E)){
            usingGizmo = 0;
        } else if(KeyListener.isKeyPressed(GLFW_KEY_R)){
            usingGizmo = 1;
        }
    }
}
