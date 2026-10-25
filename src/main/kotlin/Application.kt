import engine.GameEngine
import engine.GameClock
import input.Input
import logger.Logger
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.glClearColor
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer


object Application {

    private var window = 0L


    /**
     * Initialize everything needed for the game/application window
     **/
    fun init() {

        // Create error printing feedback
        GLFWErrorCallback.createPrint(System.err).set()
        if (!glfwInit()) throw IllegalStateException("Unable to initialize GLFW");

        // Set window hints
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window and check for validity
        window = glfwCreateWindow(854, 480, "Game", NULL, NULL)
        if (window == NULL) {
            Logger.error("Failed to create the game window long")
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(window)   // must come before createCapabilities
        GL.createCapabilities()          // must come before any gl* call
        glfwSwapInterval(1)
        glfwShowWindow(window)

        // Maintain aspect ratio
        glfwSetWindowAspectRatio(window, 16, 9);

        // Set up key callbacks
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->

            if (action == GLFW_PRESS) {
                Input.keySet.add(key)
            }

            if (action == GLFW_RELEASE) {
                Input.keySet.remove(key)
            }
        }

        // Push new frame, frames are popped automatically
        stackPush().use { stack ->
            val pWidth: IntBuffer = stack.mallocInt(1) // int*
            val pHeight: IntBuffer = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode?.width()?.minus(pWidth.get(0)))?.div(2) ?: 0,
                (vidmode?.height()?.minus(pHeight.get(0)))?.div(2) ?: 0
            )

            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);
        }

        // Initialize everything for the Engine
        GameEngine.init(window)
    }

    /**
     * The main loop function of the game.
     * Contains the while loop that sustains the game loop.
     **/
    fun loop() {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        GameClock.reset() // start timing right before the first frame

        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents()   // input first, so update sees fresh state
            GameClock.tick()   // once per frame, always before update

            // Update and render GameEngine
            GameEngine.update(GameClock.deltaTime)
            GameEngine.tick(GameClock.deltaTime)
            GameEngine.render(GameClock.deltaTime)

            // Buffers should only be swapped ONCE at the end of the frame
            glfwSwapBuffers(window)
        }
    }

    /** The main function to run the application cleanly. */
    fun run() {

        Logger.info("Starting game")
        Logger.info("LWJGL version ${Version.getVersion()}")

        init()
        loop()

        // Cleanup before terminating
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()

        // Provide context to debugger
        Logger.info("Game exited successfully")
    }
}