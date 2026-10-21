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

    private var quad: Mesh
    private var circle: Mesh

    init {
        // At the start of initializing the player, create a camera for it so that the world may have a camera to function
        entities.add(PlayerEntity(cam = Camera()))
        camera = (entities.find { it.name == "PlayerEntity" } as PlayerEntity).cam
        quad = Mesh.quad()
        circle = Mesh.circle()
        meshes.addAll(listOf(quad, circle))
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
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderer?.begin(camera)
        drawRect(0f, 0f, 2f, 1f, 0f, Vector3f(0.2f, 0.7f, 0.3f))   // green rectangle
        drawCircle(3f, 0f, 1.5f, Vector3f(0.9f, 0.4f, 0.2f))       // orange circle
        renderer?.end()
    }

    /** Spawn in an entity/entities into the world */
    fun <T : Entity> spawn(entity: T) = entities.add(entity)
    fun <T : Entity> spawn(entityList: List<T>) = entityList.forEach { spawn(it) }

    /** Remove entity/entities from the world, returns whether the element was removed (found) */
    fun <T : Entity> removeEntity(entity: T) = entities.remove(entity)
    fun <T : Entity> removeEntities(entityList: List<T>) = entityList.forEach { removeEntity(it) }

    // TODO ========= Remove later - for testing ======================================================
    private fun drawRect(x: Float, z: Float, w: Float, h: Float, rot: Float, color: Vector3f) {
        val model = Matrix4f().translate(x, 0f, z).rotateY(rot).scale(w, 1f, h)
        renderer?.submit(quad, model, color)
    }

    private fun drawCircle(x: Float, z: Float, diameter: Float, color: Vector3f) {
        val model = Matrix4f().translate(x, 0f, z).scale(diameter, 1f, diameter)
        renderer?.submit(circle, model, color)
    }
}