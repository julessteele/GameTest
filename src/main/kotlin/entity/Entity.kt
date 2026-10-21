package entity

import entity.comp.EntityFlagTypes
import entity.comp.EntityFlags
import entity.comp.EntityStats
import org.joml.Vector2f
import org.joml.Vector3f

abstract class Entity(
    var name: String,
    var maxHp: Float = 20f,
    var hp: Float = maxHp,
    var pos: Vector3f = Vector3f(),
    var width: Float = 1f,
    var height: Float = 1f) {

    /** Center of the entity, in world units. */
    val velocity = Vector2f()
    var rotation = 0f // radians

    val entityFlags = EntityFlags()
    val entityStats = EntityStats(this, maxHp, hp)

    /** Entities initialize alive and solid */
    init {
        entityFlags.addFlag(EntityFlagTypes.ENTITY_ALIVE)
        entityFlags.addFlag(EntityFlagTypes.ENTITY_SOLID)
    }

    // Axis-aligned bounds
    val left get() = pos.x - width / 2
    val right get() = pos.x + width / 2
    val bottom get() = pos.y - height / 2
    val top get() = pos.y + height / 2

    fun overlaps(o: Entity) = left < o.right && right > o.left && bottom < o.top && top > o.bottom

    // Abstract functions are below

    abstract fun tick(dt: Float)
    /** NOTE: When this function is overridden, you do not need to call the begin or end functions, as that is done by the game engine. */
    abstract fun render()
}