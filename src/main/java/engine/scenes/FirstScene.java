package engine.scenes;

import engine.Camera;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class FirstScene extends Scene{
    private int vaoId, vboId, eboID;
    private Shader defaultShader;
    private float[] vertexArray = {
            100.5f,  0.5f, 0.0f,         1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 100.5f, 0.0f,         0.0f, 1.0f, 1.0f, 1.0f,
            100.5f, 100.5f, 0.0f,        1.0f, 1.0f, 0.0f, 1.0f,
            0.5f,  0.5f, 0.0f,        0.0f, 0.0f, 0.0f, 1.0f
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
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

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
        int floatSizeInBytes = 4;
        int vertexSizeInBytes = (positionSize + colorSize) * floatSizeInBytes;

        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeInBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionSize * floatSizeInBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float deltaTime) {
        defaultShader.use();
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
    }
}
