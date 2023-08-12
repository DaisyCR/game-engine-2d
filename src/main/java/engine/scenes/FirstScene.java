package engine.scenes;

import engine.Camera;
import engine.GameObject;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.w3c.dom.Text;
import renderer.Shader;
import renderer.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class FirstScene extends Scene{
    private int vaoId, vboId, eboID;
    private Shader defaultShader;
    private Texture testTexture;

    GameObject testObj;
    private float[] vertexArray = {
    //        position                         color                    UV coords
            100.5f,  0.5f, 0.0f,         1.0f, 0.0f, 1.0f, 1.0f,        1, 1,
            0.5f, 100.5f, 0.0f,          0.0f, 1.0f, 1.0f, 1.0f,        0, 0,
            100.5f, 100.5f, 0.0f,        1.0f, 1.0f, 0.0f, 1.0f,        1, 0,
            0.5f,  0.5f, 0.0f,           0.0f, 0.0f, 0.0f, 1.0f,        0, 1
    };

    //MUST BE IN COUNTER-CLOCKWISE
    private int[] elementArray = {
            /*
            0, 1, 3,
            1, 2, 3 */
            2, 1, 0,
            0, 1, 3

    };
    public FirstScene(){

    }

    @Override
    public void init() {
        this.testObj = new GameObject("test object");

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/mario.png");

        //Generate VAO, VBO and EBO buffer objects, and send to gpu
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Create Indeces
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeInBytes = (positionSize + colorSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeInBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float deltaTime) {
        defaultShader.use();

        //Upload Texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        //Upload Camera to shader
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());

        //Bind VAO
        glBindVertexArray(vaoId);

        //Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();

        for(GameObject go : this.gameObjects){
            go.update(deltaTime);
        }
    }
}
