package engine.scenes;

public abstract class Scene {
    public Scene(){}

    public void init(){}

    public abstract void update(float deltaTime);
}