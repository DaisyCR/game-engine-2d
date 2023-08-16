package engine;

import engine.scenes.LevelEditorScene;
import engine.scenes.Scene;
import engine.scenes.SecondScene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width;
    private int height;
    private long glfwWindow;
    private String title;
    private static Window window = null;
    private static Scene currentScene;
    private ImGuiLayer imGuiLayer;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "My Engine";
    }

    public static Window get(){
        if(Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run(){
        System.out.println( "Hello LWJGL" + Version.getVersion() + "!" );

        init();
        loop();

        //Free window callbacks and destroy windows
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW and free error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        //Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW
        if( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE ); //Starts the window hidden
        glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE ); //Allows the window to be resizable
        glfwWindowHint( GLFW_MAXIMIZED, GLFW_TRUE ); //Starts thw window maximized

        //Create the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if ( glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        //Initialize Callbacks
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });


        //Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable VSYNC
        glfwSwapInterval(1);

        //Make window visible
        glfwShowWindow(glfwWindow);

        //Make OpenGL bindings available
        GL.createCapabilities();

        //Allow transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //Setup ImGUI
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);
    }



    public void loop() {
        float startFrameTime = (float) glfwGetTime();
        float deltaTime = -1.0f;

        while ( !glfwWindowShouldClose(glfwWindow) ) {
            //Poll events
            glfwPollEvents();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f); //Set window color
            glClear(GL_COLOR_BUFFER_BIT);

            if( deltaTime >= 0 ){
                currentScene.update(deltaTime);
            }

            this.imGuiLayer.update(deltaTime, currentScene);
            glfwSwapBuffers(glfwWindow); //Update buffers

            float endFrameTime = (float) glfwGetTime();
            deltaTime = endFrameTime - startFrameTime;
            startFrameTime = endFrameTime;
        }
        currentScene.saveExit();
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    // TODO Generate method on compile by searching all the Scenes in the code
    public static void changeScene(int sceneIndex){
        switch (sceneIndex) {
            case 0 -> {
                currentScene = new LevelEditorScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
            }
            case 1-> {
                currentScene = new SecondScene();
                currentScene.load();
                currentScene.init();
                currentScene.start();
            }
            default -> {
                assert false : "Unknown Scene '" + sceneIndex + "'";
            }
        }
    }

    public static int getWidth(){
        return get().width;
    }

    public static int getHeight(){
        return get().height;
    }

    private static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    private static void setHeight(int newHeight) {
        get().height = newHeight;
    }
}
