package renderer;

import components.SpriteRenderer;
import engine.GameObject;
import engine.Transform;
import engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    //Vertex
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private Renderer renderer;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer){
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
        this.zIndex = zIndex;
        this.renderer = renderer;

        //4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void render(){
        //Check if any sprites has changed and needs to be reloaded
        Shader shader = Renderer.getBoundShader();
        boolean rebufferData = false;
        for( int i = 0; i < numSprites; i++ ){
            SpriteRenderer spr = sprites[i];
            if( spr.isDirty() ){
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }

            //TODO fix this later
            if(spr.gameObject.transform.zIndex != this.zIndex){
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }

        if( rebufferData ) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        //Use Shader
        shader.use();

        //Setup Camera
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        //Setup Textures
        for( int i = 0; i < textures.size(); i++ ){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        //Bind Vertex Array buffer
        glBindVertexArray(vaoID);

        //Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Draw elements on screen
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        //Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        for( int i = 0; i < textures.size(); i++ ){
            textures.get(i).unbind();
        }
        shader.detach();
    }

    public void addSprite(SpriteRenderer sprite){
        //Get index and add sprite
        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        if( sprite.getTexture() != null ){
            if( !textures.contains(sprite.getTexture()) ){
                textures.add(sprite.getTexture());
            }
        }

        //Add properties to vertices array
        loadVertexProperties(index);
        if( numSprites >= this.maxBatchSize ){
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texID = 0;
        if( sprite.getTexture() != null ){
            for( int i = 0; i < textures.size(); i++ ){
                if(textures.get(i).equals(sprite.getTexture())){
                    texID = i + 1;
                    break;
                }
            }
        }

        //Add rotation
        Transform goTransform = sprite.gameObject.transform;
        boolean isRotated = goTransform.rotation != 0.0f;
        Matrix4f transformationMatrix = new Matrix4f().identity();
        if(isRotated){
            transformationMatrix.translate(goTransform.position.x, goTransform.position.y, 0);
            transformationMatrix.rotate((float)Math.toRadians(goTransform.rotation), 0, 0, 1);
            transformationMatrix.scale(goTransform.scale.x, goTransform.scale.y, 0);
        }

        // Add vertices with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos =  new Vector4f(goTransform.position.x + (xAdd * goTransform.scale.x), goTransform.position.y + (yAdd * goTransform.scale.y), 0, 1);
            if(isRotated){
              currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformationMatrix);
            }

            // Load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //Load texture
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;
            vertices[offset + 8] = texID;

            //Load EntityId
            vertices[offset + 9] = sprite.gameObject.getuId() + 1;


            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices(){
        //6 indeces per quad = 3 indeces per triangle
        int[] elements = new int[6 * maxBatchSize];
        for( int i = 0;  i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom(){
        return this.textures.size() < texSlots.length;
    }

    public boolean hasTexture(Texture texture){
        return this.textures.contains(texture);
    }

    public int zIndex(){
        return this.zIndex;
    }

    public boolean destroyIfExists(GameObject go) {
        //Overriding the position of a dead sprite with the next one in line
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        for(int i = 0; i < numSprites; i++){
            if(sprites[i] == spr){
                for(int j=i; j < numSprites - 1; j++){
                    sprites[j] = sprites[j+1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }
        return  false;
    }
}
