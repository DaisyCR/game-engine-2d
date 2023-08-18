package components;

import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Constants;

public class GridLines extends Component{
    @Override
    public void update(float deltaTime){
        Vector2f cameraPos = Window.getScene().camera().position;
        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();

        int firstX = ((int)(cameraPos.x / Constants.GRID_WIDTH) - 1) * Constants.GRID_HEIGHT;
        int firstY = ((int)(cameraPos.y / Constants.GRID_HEIGHT) - 1) * Constants.GRID_HEIGHT;

        int numVerticalLines = (int)(projectionSize.x / Constants.GRID_WIDTH) + 2;
        int numHorizontalLines = (int)(projectionSize.y / Constants.GRID_HEIGHT) + 2;

        int width = (int)projectionSize.x + Constants.GRID_HEIGHT * 2;
        int height =(int)projectionSize.y + Constants.GRID_WIDTH * 2;

        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
        for(int i = 0; i < maxLines; i++){
            Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
            int x = firstX + (Constants.GRID_WIDTH * i);
            int y = firstY + (Constants.GRID_HEIGHT * i);

            if(i < numVerticalLines){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numHorizontalLines){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    };
}
