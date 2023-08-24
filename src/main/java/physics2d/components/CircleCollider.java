package physics2d.components;

import components.Collider;

public class CircleCollider extends Collider {
    private float radius = 1.0f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void editorUpdate(float deltaTime) {

    }
}
