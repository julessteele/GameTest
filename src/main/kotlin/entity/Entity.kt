package entity

import entity.comp.EntityFlagTypes
import entity.comp.EntityFlags
import entity.comp.EntityStats
import org.joml.Vector2f

abstract class Entity(var name: String, var maxHp: Float = 20f, var hp: Float = maxHp, var x: Float = 0f, var y: Float = 0f, var width: Float = 1f, var height: Float = 1f) {

    /** Center of the entity, in world units. */
    val position = Vector2f(x, y)
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
    val left get() = position.x - width / 2
    val right get() = position.x + width / 2
    val bottom get() = position.y - height / 2
    val top get() = position.y + height / 2

    fun overlaps(o: Entity) = left < o.right && right > o.left && bottom < o.top && top > o.bottom

    /** MUST use super in extended classes in order for basic entity functions to work */
    open fun tick(dt: Float) {

        position.fma(dt, velocity)
    }

    open fun render() { }
}