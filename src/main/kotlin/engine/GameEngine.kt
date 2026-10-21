package engine

import entity.PlayerEntity
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.opengl.GL11C.*
import render.Renderer
import world.World

object GameEngine {

    var world: World? = null

    /** Game setup goes here. */
    fun init(window: Long) {

        // For now, assign the current world to empty "grasslands"
        world = World("grassland", mutableListOf())

        // Assign global renderer
        world?.renderer = Renderer().also { it.init() }
        glfwSetFramebufferSizeCallback(window) { _, w, h -> world?.renderer?.resize(w, h) }
    }

    /**
     * Game logic goes here.
     * At the "HEAD" the function is still running at the current refresh rate.
     * At the "TAIL" the function is running at 20 Hz
     **/
    fun update(dt: Float) {

        // The player for easy access
        val p = world?.entities?.find { it.name == "PlayerEntity" } as PlayerEntity

        // Keep camera updating at the current refresh rate
        world?.camera?.follow(p.pos.x, p.pos.y)
        world?.camera?.update()

        // Update the world timer with delta time
        GameClock.world_timer += dt
        // ========= Limit updates to our world's 20 Hz tick rate =========
        if (GameClock.world_timer >= GameClock.INTERVAL) return
        // Tick the world now that we are at 20 Hz
        world?.tick(dt)
        // Remove the current tick from the ticks left to complete
        GameClock.world_timer -= GameClock.INTERVAL
    }

    /** Controls all rendering functions for the game. */
    fun render() {

        // Clear bitmasks before rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Begin rendering
        world?.renderer?.begin(world?.camera)
        // Draw the world, entities, player, etc. will be drawn via this pipeline
        world?.render()
        // End rendering
        world?.renderer?.end()
    }
}