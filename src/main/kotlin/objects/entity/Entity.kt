package objects.entity

import objects.GameObject
import objects.entity.comp.EntityFlagTypes
import objects.entity.comp.EntityFlags
import objects.entity.comp.EntityStats
import org.joml.Vector2f
import org.joml.Vector3f
import world.World

abstract class Entity(
    name: String,
    var maxHp: Float = 20f,
    var hp: Float = maxHp,
    var pos: Vector3f = Vector3f(),
    var width: Float = 1f,
    var height: Float = 1f): GameObject(name) {

    // Axis-aligned bounds
    val left get() = pos.x - width / 2
    val right get() = pos.x + width / 2
    val bottom get() = pos.y - height / 2
    val top get() = pos.y + height / 2

    /** Center of the entity, in world units. */
    val velocity = Vector2f()
    var rotation = 0f // radians

    val entityFlags = EntityFlags(this)
    val entityStats = EntityStats(this, maxHp, hp)

    /** Entities initialize alive and solid */
    init {
        entityFlags.addFlag(EntityFlagTypes.ENTITY_ALIVE)
        entityFlags.addFlag(EntityFlagTypes.ENTITY_SOLID)
    }

    /** Check if current entity collides with o entity */
    fun overlaps(o: Entity) = left < o.right && right > o.left && bottom < o.top && top > o.bottom

    /**
     * Updates as fast as the frame rate, multiply by dt for movement.
     * Must super the function in order for velocity to work, or implement custom velocity rules.
     **/
    override fun update(dt: Float) {

        // Clamp velocity so it can't accelerate to infinity
        velocity.x = velocity.x.coerceIn(-1f, 1f)
        velocity.y = velocity.y.coerceIn(-1f, 1f)

        // Adds the velocity to the entity's movement
        this.pos.add(velocity.x, velocity.y, 0f)

        // Simulate friction
        this.pos.mul(0.9f, 0.9f, 1f)
    }

    /** The tick function for entities, runs at 20 hz */
    abstract override fun tick(dt: Float, world: World)
    /** NOTE: When this function is overridden, you do not need to call the begin or end functions, as that is done by the game engine. */
    abstract override fun render(dt: Float, world: World)
}
