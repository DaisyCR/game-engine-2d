package engine;

import editor.ImGuiLayer;
import editor.PickingTexture;
import renderer.DebugDraw;
import renderer.Framebuffer;
import renderer.Renderer;
import renderer.Shader;
import scenes.LevelEditorScene;
import scenes.Scene;
import scenes.LevelScene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.AssetPool;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private long glfwWindow;
    private String title;
    private static Window window = null;
    private static Scene currentScene;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

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
        glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE ); //Starts the window hidden
        glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE ); //Allows the window to be resizable

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
        glfwMaximizeWindow(glfwWindow);
        glfwShowWindow(glfwWindow);

        //Make OpenGL bindings available
        GL.createCapabilities();

        //Allow transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //Setup ImGUI
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        //Enable framebuffer
        this.framebuffer = new Framebuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0,0 ,1920, 1080);

        Window.changeScene(0);
    }



    public void loop() {
        float startFrameTime = (float) glfwGetTime();
        float deltaTime = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while ( !glfwWindowShouldClose(glfwWindow) ) {
            //Poll events
            glfwPollEvents();

            //Render to pick textures
            glDisable(GL_BLEND);
            pickingTexture.enableWritting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f,0.0f,0.0f,0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);
            currentScene.render();

            if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
                int x = (int)MouseListener.getScreenX();
                int y = (int)MouseListener.getScreenY();
                System.out.println(pickingTexture.readPixel(x, y));
            }

            pickingTexture.disableWritting();
            glEnable(GL_BLEND);

            //Render to show game
            DebugDraw.beginFrame();
            this.framebuffer.bind();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f); //Set window color
            glClear(GL_COLOR_BUFFER_BIT);

            if( deltaTime >= 0 ){
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(deltaTime);
                currentScene.render();
            }
            this.framebuffer.unbind();

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
                currentScene = new LevelScene();
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

    public static Framebuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getAspectRatio(){
        return 16.0f / 9.0f;
    }


    private static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    private static void setHeight(int newHeight) {
        get().height = newHeight;
    }
}
