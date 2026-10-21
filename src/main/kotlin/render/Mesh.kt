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

        /** Unit cube centered on the origin, CCW winding, per-face normals. */
        fun cube(): Mesh {
            // each face: normal(3), u axis(3), v axis(3), where u x v = normal
            val faces = arrayOf(
                floatArrayOf(0f, 0f, 1f, 1f, 0f, 0f, 0f, 1f, 0f),
                floatArrayOf(0f, 0f, -1f, -1f, 0f, 0f, 0f, 1f, 0f),
                floatArrayOf(1f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f),
                floatArrayOf(-1f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f),
                floatArrayOf(0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, -1f),
                floatArrayOf(0f, -1f, 0f, 1f, 0f, 0f, 0f, 0f, 1f),
            )
            val corners = arrayOf(-1 to -1, 1 to -1, 1 to 1, -1 to 1)
            val uvs = arrayOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)

            val verts = ArrayList<Float>()
            val idx = ArrayList<Int>()
            for ((fi, f) in faces.withIndex()) {
                for ((ci, c) in corners.withIndex()) {
                    for (axis in 0..2) {
                        verts += f[axis] * 0.5f + (c.first * f[3 + axis] + c.second * f[6 + axis]) * 0.5f
                    }
                    verts += f[0]; verts += f[1]; verts += f[2]
                    verts += uvs[ci].first; verts += uvs[ci].second
                }
                val b = fi * 4
                idx += listOf(b, b + 1, b + 2, b, b + 2, b + 3)
            }
            return Mesh(verts.toFloatArray(), idx.toIntArray())
        }
    }
}
