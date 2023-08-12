package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean isBeingUsed = false;
    private String vertexSrc, fragmentSrc, filepath;

    public Shader(String filepath){
        this.filepath = filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int endOfLine = source.indexOf("\n", index);
            String firstPattern = source.substring(index, endOfLine).trim();

            index = source.indexOf("#type", endOfLine) + 6;
            endOfLine = source.indexOf("\n", index);
            String secondPattern = source.substring(index, endOfLine).trim();

            if( firstPattern.equals("vertex") ){
                vertexSrc = splitString[1];
            } else if ( firstPattern.equals("fragment") ){
                fragmentSrc = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "' in '" + filepath + "'");
            }

            if( secondPattern.equals("vertex") ){
                vertexSrc = splitString[2];
            } else if ( secondPattern.equals("fragment") ){
                fragmentSrc = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "' in '" + filepath + "'");
            }

        } catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: " + filepath + "'";
        }
    }

    public void compile(){
        //COMPILE SHADERS
        int vertexID, fragmentID;

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if( success == GL_FALSE ){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if( success == GL_FALSE ){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //LINK SHADERS
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if( success == GL_FALSE ){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use(){
        if( !isBeingUsed ){
            glUseProgram(shaderProgramID);
            isBeingUsed = true;
        }

    }

    public void detach(){
        glUseProgram(0);
        isBeingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matbuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matbuffer);
        glUniformMatrix4fv(varLocation, false, matbuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matbuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matbuffer);
        glUniformMatrix3fv(varLocation, false, matbuffer);
    }

    public void uploadMat2f(String varName, Matrix2f mat2){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matbuffer = BufferUtils.createFloatBuffer(4);
        mat2.get(matbuffer);
        glUniformMatrix2fv(varLocation, false, matbuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}