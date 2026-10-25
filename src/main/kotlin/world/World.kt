package world

import objects.entity.Entity
import objects.entity.PlayerEntity
import logger.Logger
import objects.GameObject
import render.Camera
import render.Renderer

open class World(
    val name: String,
    val gameObjects: MutableList<GameObject>,
    var camera: Camera? = null,
    var renderer: Renderer? = null) {

    init {
        // At the start of initializing the player, create a camera for it so that the world may have a camera to function
        gameObjects.add(PlayerEntity(cam = Camera()))
        // TODO If the player is removed, the camera is then removed resulting in a crash
        camera = (gameObjects.find { it.name == "PlayerEntity" } as PlayerEntity).cam
    }

    /**
     * The update function for the world. It runs at 20 Hz
     * This is where entities are ticked and checked that they are still alive.
     * If they are not alive, they are removed from the world.
     **/
    open fun tick(dt: Float) {

        /* ===== Tick through all entities ===== */
        val entities = gameObjects.filterIsInstance<Entity>()

        entities.forEach {

            // First let the entity update
            it.tick(dt, this)

            // Then handle entity dead/alive flagging
            if (it.hp <= 0f) {
                if (!it.entityStats.f_removeAlive()) {
                    // If the flag is not removed, send an error because the entity
                    // should've already been removed since it was marked as dead
                    Logger.error("Entity was already marked as dead, but was never removed! |:3")
                }
            }

            // Remove entity from the world if it is dead
            if (it.entityFlags.isDead())
                gameObjects.remove(it)
        }
    }

    /**
     * The render function for the world.
     * World renderer begin and end functions will be automatically called.
     * Calling them again will likely cause rendering issues.
     **/
    open fun render(dt: Float) {

        // Feed world into entity renderers so they may draw themselves
        gameObjects.forEach { it.render(dt, this) }
    }

    /** Spawn in an entity/entities into the world */
    fun <T : Entity> spawn(entity: T) = gameObjects.add(entity)
    fun <T : Entity> spawn(entityList: List<T>) = entityList.forEach { spawn(it) }

    /** Remove entity/entities from the world, returns whether the element was removed (found) */
    fun <T : Entity> removeEntity(entity: T) = gameObjects.remove(entity)
    fun <T : Entity> removeEntities(entityList: List<T>) = entityList.forEach { removeEntity(it) }
}