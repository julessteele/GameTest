package render

import logger.Logger
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C.*

private class RenderCommand(
    val mesh: Mesh,
    val shader: Shader,
    val model: Matrix4f,
    val color: Vector3f,
)

class Renderer : AutoCloseable {

    val clearColor = Vector3f(0.08f, 0.09f, 0.12f)
    val lightDirection = Vector3f(-0.4f, -1f, -0.3f).normalize()

    lateinit var defaultShader: Shader
        private set

    private val queue = ArrayList<RenderCommand>(1024)
    private var camera: Camera? = null
    private val normalMatrix = Matrix3f()

    /** Call once, after GL.createCapabilities() and with the context current. */
    fun init() {

        defaultShader = Shader(DEFAULT_VERT, DEFAULT_FRAG)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
    }

    /** Hook this to the GLFW framebuffer size callback (framebuffer size, not window size, for Retina). */
    fun resize(width: Int, height: Int) {

        if (width <= 0 || height <= 0) return

        glViewport(0, 0, width, height)
        camera?.aspect = width.toFloat() / height
    }

    fun begin(camera: Camera?) {

        this.camera = camera
        camera?.update()
        queue.clear()
    }

    fun submit(mesh: Mesh?, transform: Matrix4f, color: Vector3f = Vector3f(1f), shader: Shader = defaultShader) {

        val qMesh = mesh ?: run {
            Logger.error("Render object submitted with null (nonexistent) mesh")
            return
        }

        // Copy so callers can keep reusing their own matrices
        queue += RenderCommand(qMesh, shader, Matrix4f(transform), Vector3f(color))
    }

    fun end() {

        val cam = camera ?: run {
            Logger.error("Renderer.end() called without begin()")
            return
        }

        glClearColor(clearColor.x, clearColor.y, clearColor.z, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Batch by shader, then mesh, to minimize state changes
        queue.sortWith(compareBy({ it.shader.id }, { it.mesh.vao }))

        var boundShader = -1

        for (cmd in queue) {

            if (cmd.shader.id != boundShader) {
                cmd.shader.bind()
                cmd.shader.set("uViewProj", cam.viewProjection)
                cmd.shader.set("uLightDir", lightDirection)
                boundShader = cmd.shader.id
            }

            cmd.shader.set("uModel", cmd.model)
            cmd.model.normal(normalMatrix)
            cmd.shader.set("uNormalMatrix", normalMatrix)
            cmd.shader.set("uColor", cmd.color)
            cmd.mesh.draw()
        }
        glBindVertexArray(0)
    }

    override fun close() = defaultShader.close()


    // Provide companion object for use of all default shader constants
    private companion object {

        // The default Vertex Shader
        const val DEFAULT_VERT = """
            #version 410 core
            layout(location = 0) in vec3 aPos;
            layout(location = 1) in vec3 aNormal;
            layout(location = 2) in vec2 aUV;
 
            uniform mat4 uViewProj;
            uniform mat4 uModel;
            uniform mat3 uNormalMatrix;
 
            out vec3 vNormal;
            out vec2 vUV;
 
            void main() {
                vNormal = normalize(uNormalMatrix * aNormal);
                vUV = aUV;
                gl_Position = uViewProj * uModel * vec4(aPos, 1.0);
            }
        """

        // The default fragment shader
        const val DEFAULT_FRAG = """
            #version 410 core
            in vec3 vNormal;
            in vec2 vUV;
 
            uniform vec3 uColor;
            uniform vec3 uLightDir;
 
            out vec4 FragColor;
 
            void main() {
                float diffuse = max(dot(normalize(vNormal), normalize(-uLightDir)), 0.0);
                vec3 lit = uColor * (0.2 + 0.8 * diffuse);
                FragColor = vec4(lit, 1.0);
            }
        """
    }
}
