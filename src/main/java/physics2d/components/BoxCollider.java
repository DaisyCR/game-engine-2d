package physics2d.components;

import components.Collider;
import org.joml.Vector2f;
import renderer.DebugDraw;
import util.Settings;

public class BoxCollider extends Collider {
    private Vector2f halfSize = new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT);
    private Vector2f origin = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin() {
        return this.origin;
    }

    @Override
    public void editorUpdate(float deltaTime) {
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }
}