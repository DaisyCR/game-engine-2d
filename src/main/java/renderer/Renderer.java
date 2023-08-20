package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

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
            if( batch.hasRoom() && batch.zIndex() == sprite.gameObject.zIndex() ){
                Texture texture = sprite.getTexture();
                if(texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    isAdded = true;
                    break;
                }
            }
        }

        if( !isAdded ){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render(){
        currentShader.use();
        for( RenderBatch batch : batches ){
            batch.render();
        }
    }

    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    public static Shader getBoundShader(){
        return currentShader;
    }
}
