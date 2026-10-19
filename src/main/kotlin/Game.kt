import engine.GameClock
import logger.Logger
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11C.glClear
import org.lwjgl.opengl.GL11C.glClearColor
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.IntBuffer
import kotlin.math.sin


object Game {

    private var window = 0L
    private var time = 0f

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

        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Create window and check for validity
        window = GLFW.glfwCreateWindow(400, 400, "Game", NULL, NULL)
        if (window == NULL) {
            Logger.error("Failed to create the game window long")
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Set up key callbacks
        GLFW.glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);
        }

        // Push new frame, frames are popped automatically
        stackPush().use { stack ->
            val pWidth: IntBuffer = stack.mallocInt(1) // int*
            val pHeight: IntBuffer = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode?.width()?.minus(pWidth.get(0)))?.div(2) ?: 0,
                (vidmode?.height()?.minus(pHeight.get(0)))?.div(2) ?: 0
            )

            // Make the OpenGL context current
            GLFW.glfwMakeContextCurrent(window);
            // Enable v-sync
            GLFW.glfwSwapInterval(1);

            // Make the window visible
            GLFW.glfwShowWindow(window);
        }
    }

    fun loop() {

        GL.createCapabilities()
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f)

        GameClock.reset() // start timing right before the first frame

        while (!glfwWindowShouldClose(window)) {
            GLFW.glfwPollEvents()   // input first, so update sees fresh state
            GameClock.tick()        // once per frame, always before update

            update(GameClock.deltaTime)
            render()

            GLFW.glfwSwapBuffers(window)
        }
    }

    private fun update(dt: Float) {
        time += dt
        // sine wave oscillates between 0 and 1, one full cycle every 2 seconds
        val pulse = (sin(time * Math.PI).toFloat() + 1f) / 2f
        glClearColor(pulse, pulse, pulse, 0f)
    }

    private fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        // draw calls go here
    }
}