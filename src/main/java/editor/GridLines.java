package editor;

import components.Component;
import engine.Camera;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Constants;

public class GridLines extends Component {
    @Override
    public void editorUpdate(float deltaTime){
        Camera camera = Window.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((cameraPos.x / Constants.GRID_WIDTH.getValue()) - 1) * Constants.GRID_HEIGHT.getValue();
        float firstY = ((cameraPos.y / Constants.GRID_HEIGHT.getValue()) - 1) * Constants.GRID_HEIGHT.getValue();

        int numVerticalLines = (int)(projectionSize.x * camera.getZoom() / Constants.GRID_WIDTH.getValue()) + 2;
        int numHorizontalLines = (int)(projectionSize.y * camera.getZoom() / Constants.GRID_HEIGHT.getValue()) + 2;

        float width = (projectionSize.x * camera.getZoom()) + Constants.GRID_HEIGHT.getValue() * 2;
        float height =(projectionSize.y * camera.getZoom()) + Constants.GRID_WIDTH.getValue() * 2;

        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
        for(int i = 0; i < maxLines; i++){
            Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
            float x = firstX + (Constants.GRID_WIDTH.getValue() * i);
            float y = firstY + (Constants.GRID_HEIGHT.getValue() * i);

            if(i < numVerticalLines){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numHorizontalLines){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    };
}
