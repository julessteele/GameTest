package render

import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.system.MemoryStack

class Shader(vertexSrc: String, fragmentSrc: String) : AutoCloseable {

    val id: Int
    private val uniformCache = HashMap<String, Int>()

    init {
        val vs = compile(GL_VERTEX_SHADER, vertexSrc)
        val fs = compile(GL_FRAGMENT_SHADER, fragmentSrc)
        id = glCreateProgram()
        glAttachShader(id, vs)
        glAttachShader(id, fs)
        glLinkProgram(id)
        check(glGetProgrami(id, GL_LINK_STATUS) != GL_FALSE) {
            "Shader link failed:\n${glGetProgramInfoLog(id)}"
        }
        glDeleteShader(vs)
        glDeleteShader(fs)
    }

    private fun compile(type: Int, src: String): Int {

        val shader = glCreateShader(type)
        glShaderSource(shader, src)
        glCompileShader(shader)

        check(glGetShaderi(shader, GL_COMPILE_STATUS) != GL_FALSE) {
            "Shader compile failed:\n${glGetShaderInfoLog(shader)}"
        }

        return shader
    }

    fun bind() = glUseProgram(id)

    private fun loc(name: String): Int =
        uniformCache.getOrPut(name) { glGetUniformLocation(id, name) }

    fun set(name: String, v: Float) =
        glUniform1f(loc(name), v)
    fun set(name: String, v: Int) =
        glUniform1i(loc(name), v)
    fun set(name: String, v: Vector3f) =
        glUniform3f(loc(name), v.x, v.y, v.z)

    fun set(name: String, m: Matrix4f) =
        MemoryStack.stackPush().use { stack ->
            val buf = stack.mallocFloat(16)
            m.get(buf)
            glUniformMatrix4fv(loc(name), false, buf)
        }

    fun set(name: String, m: Matrix3f) =
        MemoryStack.stackPush().use { stack ->
            val buf = stack.mallocFloat(9)
            m.get(buf)
            glUniformMatrix3fv(loc(name), false, buf)
        }

    override fun close() = glDeleteProgram(id)
}
