package render

import logger.Logger
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C.*

class Renderer : AutoCloseable {

    val clearColor = Vector3f(0f, 0f, 0f)
    val lightDirection = Vector3f(-0.4f, -1f, -0.3f).normalize()

    lateinit var defaultShader: Shader
        private set
    lateinit var spriteShader: Shader
        private set

    private val queue = ArrayList<RenderCommand>(1024)
    private var camera: Camera? = null
    private val normalMatrix = Matrix3f()

    /** Call once, after GL.createCapabilities() and with the context current. */
    fun init() {

        defaultShader = Shader(DEFAULT_VERT, DEFAULT_FRAG)
        spriteShader = Shader(SPRITE_VERT, SPRITE_FRAG)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        glFrontFace(GL_CCW)
    }

    /** Hook this to the GLFW framebuffer size callback (framebuffer size, not window size, for Retina). */
    fun resize(width: Int, height: Int) {

        if (width <= 0 || height <= 0) return

        glViewport(0, 0, width, height)
        camera?.aspectRatio = width.toFloat() / height
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

    fun submitSprite(mesh: Mesh?, transform: Matrix4f, tex: Texture, uv: Vector4f = Vector4f(0f, 0f, 1f, 1f)) {

        val qMesh = mesh ?: run {
            Logger.error("Sprite submitted with null mesh")
            return
        }

        queue += RenderCommand(qMesh, spriteShader, Matrix4f(transform), Vector3f(1f), tex, Vector4f(uv))
    }

    fun end() {

        val cam = camera ?: run {
            Logger.error("Renderer.end() called without begin()")
            return
        }

        glClearColor(clearColor.x, clearColor.y, clearColor.z, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Batch by shader, then mesh, to minimize state changes
        queue.sortWith(compareBy({ it.shader.id }, { it.tex?.id ?: 0 }, { it.mesh.vao }))

        var boundShader = -1

        for (cmd in queue) {

            if (cmd.shader.id != boundShader) {
                cmd.shader.bind()
                cmd.shader.set("uViewProj", cam.viewProjection)
                if (cmd.tex == null) cmd.shader.set("uLightDir", lightDirection)
                boundShader = cmd.shader.id
            }

            cmd.shader.set("uModel", cmd.model)

            if (cmd.tex != null) {
                cmd.tex.bind(0)
                cmd.shader.set("uTex", 0)
                cmd.shader.set("uUV", cmd.uv)
            } else {
                cmd.model.normal(normalMatrix)
                cmd.shader.set("uNormalMatrix", normalMatrix)
                cmd.shader.set("uColor", cmd.color)
            }
            cmd.mesh.draw()
        }

        glBindVertexArray(0)
    }

    override fun close() {

        defaultShader.close()
        spriteShader.close()
    }

    // Provide companion object for use of all default shader constants
    private companion object {

        // The default Vertex Shader
        val DEFAULT_VERT = """
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
        """.trimIndent()

        // The default fragment shader
        val DEFAULT_FRAG = """
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
        """.trimIndent()

        // Vertex shader for sprites
        val SPRITE_VERT = """
            #version 410 core
            layout(location = 0) in vec3 aPos;
            layout(location = 2) in vec2 aUV;
        
            uniform mat4 uViewProj;
            uniform mat4 uModel;
            uniform vec4 uUV; // u0, v0, u1, v1
        
            out vec2 vUV;
        
            void main() {
                vUV = mix(uUV.xy, uUV.zw, aUV);
                gl_Position = uViewProj * uModel * vec4(aPos, 1.0);
            }
        """.trimIndent()

        // Fragment shader for sprites
        val SPRITE_FRAG = """
            #version 410 core
            in vec2 vUV;
            uniform sampler2D uTex;
            out vec4 color;
        
            void main() {
                color = texture(uTex, vUV);
                if (color.a < 0.5) discard;
            }
        """.trimIndent()
    }
}

private class RenderCommand(
    val mesh: Mesh,
    val shader: Shader,
    val model: Matrix4f,
    val color: Vector3f,
    val tex: Texture? = null,
    val uv: Vector4f = Vector4f(0f, 0f, 1f, 1f)
)
