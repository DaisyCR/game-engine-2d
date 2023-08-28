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
                activeGameobject.transform.scale.x -= MouseListener.getWorldX();
            } else if (yAxisActive && !xAxisActive){
                activeGameobject.transform.scale.y -= MouseListener.getWorldY();
            }
        }

        super.editorUpdate(deltaTime);
    }
}
