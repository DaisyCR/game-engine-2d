package engine.scenes;

import engine.Camera;
import engine.GameObject;
import org.joml.Vector2f;

public class FirstScene extends Scene{
    public FirstScene(){

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
    }

    @Override
    public void update(float deltaTime) {
        System.out.println("FPS: " + (1 / deltaTime));
        for(GameObject go : this.gameObjects){
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}
