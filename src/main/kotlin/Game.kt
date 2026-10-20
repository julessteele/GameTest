import engine.Engine
import engine.GameClock
import logger.Logger
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.glClearColor
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer


object Game {

    private var window = 0L

    fun run() {

        Logger.info("Starting game")
        Logger.info("LWJGL version ${Version.getVersion()}")

        init()
        loop()

        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
        glfwTerminate()
    }

    fun init() {

        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) throw IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window and check for validity
        window = glfwCreateWindow(400, 400, "Game", NULL, NULL)
        if (window == NULL) {
            Logger.error("Failed to create the game window long")
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Set up key callbacks
        glfwSetKeyCallback(window) { window, key, /* scancode */ _, action, /* mods */ _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
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
    }

    fun loop() {

        GL.createCapabilities()
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        GameClock.reset() // start timing right before the first frame

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents()   // input first, so update sees fresh state
            GameClock.tick()        // once per frame, always before update

            Engine.update(GameClock.deltaTime)
            Engine.render()

            glfwSwapBuffers(window)
        }
    }
}