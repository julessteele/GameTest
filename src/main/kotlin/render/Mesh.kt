package render

import org.lwjgl.opengl.GL30C.*

class Mesh(vertices: FloatArray, indices: IntArray) : AutoCloseable {

    val vao: Int = glGenVertexArrays()
    private val vbo: Int = glGenBuffers()
    private val ebo: Int = glGenBuffers()
    val indexCount: Int = indices.size

    init {

        glBindVertexArray(vao)

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        val stride = 8 * Float.SIZE_BYTES
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0L)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6L * Float.SIZE_BYTES)
        glEnableVertexAttribArray(2)

        glBindVertexArray(0)
    }

    fun draw() {

        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0L)
    }

    override fun close() {

        glDeleteBuffers(vbo)
        glDeleteBuffers(ebo)
        glDeleteVertexArrays(vao)
    }

    companion object {

        /** Unit square in the XZ plane, normal +Y, CCW when viewed from above. */
        fun quad(): Mesh {
            val n = floatArrayOf(0f, 1f, 0f)
            val verts = floatArrayOf(
                -0.5f, 0f, -0.5f, n[0], n[1], n[2], 0f, 0f,
                0.5f, 0f, -0.5f, n[0], n[1], n[2], 1f, 0f,
                0.5f, 0f,  0.5f, n[0], n[1], n[2], 1f, 1f,
                -0.5f, 0f,  0.5f, n[0], n[1], n[2], 0f, 1f,
            )
            return Mesh(verts, intArrayOf(0, 2, 1, 0, 3, 2))
        }

        /** Unit-diameter circle in the XZ plane (triangle fan). */
        fun circle(segments: Int = 32): Mesh {
            val verts = ArrayList<Float>()
            val idx = ArrayList<Int>()
            verts += listOf(0f, 0f, 0f, 0f, 1f, 0f, 0.5f, 0.5f) // center
            for (i in 0..segments) {
                val a = (i.toFloat() / segments) * 2f * Math.PI.toFloat()
                val c = kotlin.math.cos(a)
                val s = kotlin.math.sin(a)
                verts += listOf(c * 0.5f, 0f, -s * 0.5f, 0f, 1f, 0f, c * 0.5f + 0.5f, s * 0.5f + 0.5f)
            }
            for (i in 1..segments) { idx += listOf(0, i, i + 1) }
            return Mesh(verts.toFloatArray(), idx.toIntArray())
        }
    }
}
