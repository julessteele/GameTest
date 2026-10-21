package engine

import entity.comp.EntityFlagTypes
import input.Input
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.opengl.GL11C.*
import render.Camera
import render.Mesh
import render.Renderer
import world.World

object GameEngine {

    val world = World("grassland", mutableListOf())

    private val cube = Mesh.cube()

    /** --------- Game setup goes here --------- */
    fun init(window: Long) {

        // Assign global renderer, camera, and mesh
        world.renderer = Renderer().also { it.init() }
        world.camera = Camera()
        world.meshes.add(cube)
        glfwSetFramebufferSizeCallback(window) { _, w, h -> world.renderer?.resize(w, h) }
    }

    /** --------- Game logic goes here --------- */
    fun update(dt: Float) {

        /** ===== Update all entities ===== */
        for (entity in world.entities) {
            entity.update(dt)

            // Remove self from world if dead
            if (entity.entityFlags.findFlag(EntityFlagTypes.ENTITY_ALIVE) == null)
                world.entities.remove(entity)
        }

        if (Input.isPressed(GLFW_KEY_W))
            world.camera?.position?.add(0f, 0.1f, 0f)
        if (Input.isPressed(GLFW_KEY_S))
            world.camera?.position?.add(0f, -0.1f, 0f)
        if (Input.isPressed(GLFW_KEY_A))
            world.camera?.position?.add(-0.1f, 0f, 0f)
        if (Input.isPressed(GLFW_KEY_D))
            world.camera?.position?.add(0.1f, 0f, 0f)
    }

    fun render() {

        /** ------ Clear bitmasks before rendering ------ */
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        /* ---------- Draw calls go here ---------- */
        val model = Matrix4f()//.rotateY(GameClock.totalTime.toFloat()).translate(0f, 0f, 0f)
        world.renderer?.begin(world.camera)
        world.renderer?.submit(cube, model, Vector3f(0.9f, 0.4f, 0.2f))
        world.renderer?.end()
    }
}