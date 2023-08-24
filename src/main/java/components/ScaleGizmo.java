package components;

import editor.PropertiesWindow;
import engine.MouseListener;

public class ScaleGizmo extends Gizmo{

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float deltaTime){
        if(activeGameobject != null){
            if(xAxisActive && !yAxisActive){
                activeGameobject.transform.scale.x -= MouseListener.getWorldDeltaX();
            } else if (yAxisActive && !xAxisActive){
                activeGameobject.transform.scale.y -= MouseListener.getWorldDeltaY();
            }
        }

        super.editorUpdate(deltaTime);
    }
}
