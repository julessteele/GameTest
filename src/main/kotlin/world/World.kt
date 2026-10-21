package world

import entity.Entity
import input.Input
import logger.Logger
import org.lwjgl.glfw.GLFW.*
import render.Camera
import render.Mesh
import render.Renderer

open class World(
    val name: String,
    val entities: MutableList<Entity>,
    var camera: Camera? = null,
    var renderer: Renderer? = null,
    var meshes: MutableList<Mesh> = mutableListOf()) {

    init {
    }

    /**
     * The update function for the world. It runs at 20 Hz
     * This is where entities are ticked and checked that they are still alive.
     * If they are not alive, they are removed from the world.
     **/
    fun tick(dt: Float) {

        /* ===== Tick through all entities ===== */
        entities.forEach {

            // First let the entity update
            it.tick(dt)

            // Then handle entity dead/alive flagging
            if (it.hp <= 0f ) {
                if (!it.entityStats.f_removeAlive()) {
                    // If the flag is not removed, send an error because the entity
                    // should've already been removed since it was marked as dead
                    Logger.error("Entity was already marked as dead, but was never removed!")
                }
            }

            // Remove self from world if dead
            if (it.entityFlags.isDead())
                entities.remove(it)
        }

        // TODO Testing camera movement
        if (Input.isPressed(GLFW_KEY_W))
            camera?.position?.add(0f, 0.1f, 0f)
        if (Input.isPressed(GLFW_KEY_S))
            camera?.position?.add(0f, -0.1f, 0f)
        if (Input.isPressed(GLFW_KEY_A))
            camera?.position?.add(-0.1f, 0f, 0f)
        if (Input.isPressed(GLFW_KEY_D))
            camera?.position?.add(0.1f, 0f, 0f)
    }

    /** Spawn in an entity/entities into the world */
    fun <T : Entity> spawn(entity: T) = entities.add(entity)
    fun <T : Entity> spawn(entityList: List<T>) = entityList.forEach { spawn(it) }

    /** Remove entity/entities from the world, returns whether the element was removed (found) */
    fun <T : Entity> removeEntity(entity: T) = entities.remove(entity)
    fun <T : Entity> removeEntities(entityList: List<T>) = entityList.forEach { removeEntity(it) }
}