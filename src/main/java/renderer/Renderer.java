package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        if( sprite != null ){
            add(sprite);
        }
    }

    private void add(SpriteRenderer sprite){
        boolean isAdded = false;
        for( RenderBatch batch : batches ){
            if( batch.hasRoom() ){
                batch.addSprite(sprite);
                isAdded = true;
                break;
            }
        }

        if( !isAdded ){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render(){
        for( RenderBatch batch : batches ){
            batch.render();
        }
    }
}
