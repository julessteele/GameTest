package world

import entity.Entity
import entity.PlayerEntity
import logger.Logger
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11C.*
import render.Camera
import render.Mesh
import render.Renderer

open class World(
    val name: String,
    val entities: MutableList<Entity>,
    var camera: Camera? = null,
    var renderer: Renderer? = null,
    meshes: MutableList<Mesh> = mutableListOf()) {

    init {
        // At the start of initializing the player, create a camera for it so that the world may have a camera to function
        entities.add(PlayerEntity(cam = Camera()))
        // TODO If the player is removed, the camera is then removed resulting in a crash
        camera = (entities.find { it.name == "PlayerEntity" } as PlayerEntity).cam
    }

    /**
     * The update function for the world. It runs at 20 Hz
     * This is where entities are ticked and checked that they are still alive.
     * If they are not alive, they are removed from the world.
     **/
    open fun tick(dt: Float) {

        /* ===== Tick through all entities ===== */
        entities.forEach {

            // First let the entity update
            it.tick(dt)

            // Then handle entity dead/alive flagging
            if (it.hp <= 0f ) {
                if (!it.entityStats.f_removeAlive()) {
                    // If the flag is not removed, send an error because the entity
                    // should've already been removed since it was marked as dead
                    Logger.error("Entity was already marked as dead, but was never removed! |:3")
                }
            }

            // Remove self from world if dead
            if (it.entityFlags.isDead())
                entities.remove(it)
        }
    }

    /** The render function for the world */
    open fun render() {

        entities.forEach { it.render(this) }
    }

    /** Spawn in an entity/entities into the world */
    fun <T : Entity> spawn(entity: T) = entities.add(entity)
    fun <T : Entity> spawn(entityList: List<T>) = entityList.forEach { spawn(it) }

    /** Remove entity/entities from the world, returns whether the element was removed (found) */
    fun <T : Entity> removeEntity(entity: T) = entities.remove(entity)
    fun <T : Entity> removeEntities(entityList: List<T>) = entityList.forEach { removeEntity(it) }
}