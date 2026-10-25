package engine

import objects.entity.PlayerEntity
import logger.Logger
import objects.entity.Entity
import org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback
import org.lwjgl.opengl.GL11C.*
import render.Renderer
import world.World

object GameEngine {

    /** The list of worlds loaded for use */
    private val loadedWorlds = mutableListOf<World>()
    /** The current world for the level */
    var world: World? = loadedWorlds.firstOrNull()
        private set

    /** Initial game setup goes here. (e.g. main menu when that task is complete) */
    fun init(window: Long) {

        // For now, assign the current world to empty "grasslands"
        world = World("grassland", mutableListOf())

        // Assign global renderer
        world?.renderer = Renderer().also { it.init() }
        glfwSetFramebufferSizeCallback(window) { _, w, h -> world?.renderer?.resize(w, h) }
    }

    /**
     * Game logic goes here.
     * Update is called first in the game loop, then tick, then render.
     **/
    fun update(dt: Float) {

        // Immediately update all entities
        world?.gameObjects?.filterIsInstance<Entity>()?.forEach { it.update(dt) }

        // The player for easy access
        val p = world?.gameObjects?.find { it.name == "PlayerEntity" } as PlayerEntity

        // Keep camera updating at the current refresh rate
        world?.camera?.follow(p.pos.x, p.pos.y)
        world?.camera?.update()
    }

    /**
     * The tick function for the engine's current world, ticks at 20 hz to reduce cpu load.
     * This is ideally used for random game events, updating the world, entity AI, etc.
     */
    fun tick(dt: Float) {

        // Update the world timer with delta time since tick() is called at the same frequency as update.
        GameClock.world_timer += dt

        /*
        Now limit updates to our world's 20 Hz tick rate, otherwise
        break out of the function and do not tick the world.
         */
        if (GameClock.world_timer >= GameClock.INTERVAL) return

        // Tick the world now that we are at 20 Hz
        world?.tick(dt)

        // Remove the current "tick" from the "remaining ticks" the engine has to complete
        // This allows lag instead of skipping time in game when the CPU can't keep up
        GameClock.world_timer -= GameClock.INTERVAL
    }

    /**
     * Controls all rendering functions for the game.
     * Is called immediately after GameEngine.tick() in the main game loop.
     **/
    fun render(dt: Float) {

        // Clear bitmasks before rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Begin rendering
        world?.renderer?.begin(world?.camera)
        // Draw the world, entities, player, etc. will be drawn via this pipeline
        world?.render(dt)
        // End rendering
        world?.renderer?.end()
    }

    /**
     * Adds 'w' world to the loaded worlds list and assigns it as the first element in the list.
     * Then the function sets the current loaded world to the first world in the index.
     **/
    fun preloadWorld(w: World) {

        // Add provided world at the first index
        this.loadedWorlds.add(0, w)

        // Send error message and break if the world is somehow null
        if (this.loadedWorlds.firstOrNull() == null) {
            Logger.error("World \"${w.name}\" not found. New world will be assigned.")
            Logger.warning("World \"$w\" has been added to the list, but was null.")
            return // break out of function
        }

        // Assign world if successful
        this.world = loadedWorlds.first()
        // Give debug info
        Logger.info("World \"${w.name}\" has been added to the list and the current world was assigned to \"${this.world?.name}\".")
    }

    /**
     * Set the current world to 'w' world, where
     * 'w' must be in the list of loaded worlds.
     */
    fun setWorld(w: World): Boolean {

        // Iterate through our loaded worlds
        loadedWorlds.forEach {
            if (it == w) {
                // If we find a match assign the world to it
                this.world = it
                // Give feedback that the world was successfully loaded
                Logger.info("World \"${it.name}\" has been loaded from the worlds list.")
                return true
            }
        }

        // Otherwise return false and give feedback
        Logger.warning("World \"${w.name}\" has not been found in the list, and was thus not loaded.")
        return false
    }

    /**
     * Adds a world (w) to the list of loaded worlds.
     * Worlds must be loaded before they can be set.
     */
    fun loadWorld(w: World) = this.loadedWorlds.add(w)

    /**
     * Removes a world from the loaded worlds list.
     */
    fun dispose(world: World) = this.loadedWorlds.remove(world)

    /**
     * Dumps all the worlds from memory except the current world.
     */
    fun dump() {
        this.loadedWorlds.forEach { w ->
            if (w != world) {
                this.loadedWorlds.remove(w)
            }
        }
        Logger.debug("Dumped all worlds except the current one.")
    }
}
