package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
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
        glUseProgram(shaderProgramID);
    }

    public void detach(){
        glUseProgram(0);
    }
}