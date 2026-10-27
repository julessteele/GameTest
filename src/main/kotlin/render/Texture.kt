package render

import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack

class Texture private constructor(val id: Int, val width: Int, val height: Int) : AutoCloseable {

    fun bind(unit: Int = 0) {

        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    override fun close() = glDeleteTextures(id)

    companion object {

        fun fromFile(path: String): Texture = MemoryStack.stackPush().use { stack ->

            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val c = stack.mallocInt(1)

            stbi_set_flip_vertically_on_load(true)

            val pixels = stbi_load("src/main/resources/$path", w, h, c, 4)
                ?: error("Failed to load $path: ${stbi_failure_reason()}")

            val id = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, id)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w[0], h[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
            stbi_image_free(pixels)

            Texture(id, w[0], h[0])
        }
    }
}