package engine

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.opengl.GL11C.*
import render.Camera
import render.Mesh
import render.Renderer
import world.World

object GameEngine {

    var world: World? = null

    @Deprecated("Temporarily here for camera reference.")
    private val cube = Mesh.cube()

    /** Game setup goes here. */
    fun init(window: Long) {

        // For now, assign the current world to empty "grasslands"
        world = World("grassland", mutableListOf())

        // Assign global renderer, camera, and mesh
        world?.renderer = Renderer().also { it.init() }
        world?.camera = Camera()
        world?.meshes?.add(cube)
        glfwSetFramebufferSizeCallback(window) { _, w, h -> world?.renderer?.resize(w, h) }
    }

    /**
     * Game logic goes here.
     * At the "HEAD" the function is still running at the current refresh rate.
     * At the "TAIL" the function is running at 20 Hz
     **/
    fun update(dt: Float) {

        // Update the world timer with delta time
        GameClock.world_timer += dt

        // Limit updates to our world's 20 Hz tick rate
        if (GameClock.world_timer >= GameClock.INTERVAL) return
        // Tick the world now that we are at 20 Hz
        world?.tick(dt)
        // Remove the current tick from the ticks left to complete
        GameClock.world_timer -= GameClock.INTERVAL
    }

    /** Controls all rendering functions for the game. */
    fun render() {

        /* ------ Clear bitmasks before rendering ------ */
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        /* ---------- Draw calls go here ---------- */
        val model = Matrix4f()//.rotateY(GameClock.totalTime.toFloat()).translate(0f, 0f, 0f)
        world?.renderer?.begin(world?.camera)
        world?.renderer?.submit(cube, model, Vector3f(0.9f, 0.4f, 0.2f))
        world?.renderer?.end()
    }
}