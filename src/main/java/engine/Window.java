package engine;

import editor.ImGuiLayer;
import editor.PickingTexture;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {
    private int width, height;
    private long glfwWindow;
    private String title;
    private static Window window = null;
    private static long audioContext;
    private static long audioDevice;
    private static Scene currentScene;
    private static ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean isRuntimePlaying = false;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "My Engine";
        EventSystem.addObserver(this);
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

        //Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

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

        //Initialize Audio
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        if(!alCapabilities.OpenAL10){
            assert false : "Audio library not supported";
        }

        //Make OpenGL bindings available
        GL.createCapabilities();

        //Allow transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //Enable framebuffer
        this.framebuffer = new Framebuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0,0 ,1920, 1080);

        //Setup ImGUI
        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer());
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
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f,0.0f,0.0f,0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            //Render to show game
            DebugDraw.beginFrame();
            this.framebuffer.bind();
            glClearColor(1.0f, 1.0f, 1.0f, 1.0f); //Set window color
            glClear(GL_COLOR_BUFFER_BIT);

            if( deltaTime >= 0 ){
                Renderer.bindShader(defaultShader);
                if(isRuntimePlaying){
                    currentScene.update(deltaTime);
                } else {
                    currentScene.editorUpdate(deltaTime);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            this.framebuffer.unbind();

            this.imGuiLayer.update(deltaTime, currentScene);

            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow); //Update buffers
            MouseListener.endFrame();

            float endFrameTime = (float) glfwGetTime();
            deltaTime = endFrameTime - startFrameTime;
            startFrameTime = endFrameTime;
            double fps = Math.round((1 / deltaTime));
            glfwSetWindowTitle(glfwWindow, "My Engine | FPS: " + fps + "");

        }
    }

    public static ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    public static Scene getScene(){
        return get().currentScene;
    }

    // TODO Generate method on compile by searching all the Scenes in the code
    public static void changeScene(SceneInitializer sceneInitializer){
        if(currentScene != null){
            currentScene.destroy();
        }
        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static int getWidth(){
        return 1920;//get().width;
    }

    public static int getHeight(){
        return 1080;//get().height;
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

    @Override
    public void onNotify(GameObject gameObject, Event event) {
        switch(event.type){
            case GameEngineStartPlay -> {
                this.isRuntimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
            }
            case GameEngineStopPlay -> {
                this.isRuntimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
            }
            case SaveLevel -> currentScene.save();
            case LoadLevel -> Window.changeScene(new LevelEditorSceneInitializer());
            case UserEvent -> {}
        }
    }
}
