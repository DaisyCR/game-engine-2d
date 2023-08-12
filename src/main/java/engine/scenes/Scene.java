package engine.scenes;

import engine.Camera;

public abstract class Scene {

    protected Camera camera;
    public Scene(){}

    public void init(){}

    public abstract void update(float deltaTime);
}
