package engine.renderer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;

import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

public class Window {

    private long window;
    private int width;
    private int height;

    private boolean tic = true;

    private GameLoop gameLoop;

    public Window(int height, int width, GameLoop gameLoop) {
        this.height = height;
        this.width = width;
        this.gameLoop = gameLoop;
    }

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public int[] getWindowSize() {

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(window, w, h);

            return new int[] {w.get(0),h.get(0)};
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        //Init glfw
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        //16x MSAA Antialiasing
        glfwWindowHint(GLFW_SAMPLES, 16);

        //Create the actual window
        window = glfwCreateWindow(width, height, "PBR Rendering", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        gameLoop.window = window;

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            gameLoop.onKey(key, action);
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);
    }

    private void loop() {
        createCapabilities();
        gameLoop.onPrep();
        while ( !glfwWindowShouldClose(window) ) {
            gameLoop.onUpdate();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        gameLoop.onExit();
    }

    public long getWindow() {
        return window;
    }
}
