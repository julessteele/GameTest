package engine

import entity.Entity
import entity.flags.EntityFlagTypes
import org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT
import org.lwjgl.opengl.GL11C.glClear

object Engine {

    private val entities = mutableListOf<Entity>()

    fun update(dt: Float) {

        /** --------- Game logic goes here --------- */

        // Update all entities
        for (entity in entities) {
            entity.update(dt)

            // Kill self if dead
            if (entity.entityFlags.findFlag(EntityFlagTypes.ENTITY_ALIVE) == null)
                entities.remove(entity)
        }
    }

    fun render() {

        // Clear bitmasks before rendering
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        /** --------- Draw calls go here --------- */

    }
}