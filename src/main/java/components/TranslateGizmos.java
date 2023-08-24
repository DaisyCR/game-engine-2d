package components;

import editor.PropertiesWindow;
import engine.MouseListener;

public class TranslateGizmos extends Gizmo{

    public TranslateGizmos(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float deltaTime){
        if(activeGameobject != null){
            if(xAxisActive && !yAxisActive){
                activeGameobject.transform.position.x -= MouseListener.getWorldDeltaX();
            } else if (yAxisActive && !xAxisActive){
                activeGameobject.transform.position.y -= MouseListener.getWorldDeltaY();
            }
        }

        super.editorUpdate(deltaTime);
    }
}
