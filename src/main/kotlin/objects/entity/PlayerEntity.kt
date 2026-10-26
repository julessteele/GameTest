package objects.entity

import input.Input
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import render.Camera
import render.ColorVecs
import render.Mesh
import world.World

class PlayerEntity(
    name: String = "PlayerEntity",
    maxHp: Float = 20f,
    hp: Float = maxHp,
    pos: Vector3f = Vector3f(),
    width: Float = 1f,
    height: Float = 1f,
    var cam: Camera? = null) :
    Entity(name, maxHp, hp, pos, width, height) {

    private val moveSpeed = 100f
    private var quad: Mesh = Mesh.quad()
    private var circle: Mesh = Mesh.circle()

    override fun update(dt: Float) {

        super.update(dt)

        // ==== Handle player movement ====
        if (Input.isPressed(GLFW_KEY_W))
            this.velocity.y += moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_S))
            this.velocity.y -= moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_A))
            this.velocity.x -= moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_D))
            this.velocity.x += moveSpeed * dt
    }

    override fun tick(dt: Float, world: World) {
    }

    override fun render(dt: Float, world: World) {

        // TODO ========= Remove later - for testing ======================================================
        fun drawRect(pos: Vector3f, w: Float, h: Float, rot: Float, color: Vector3f) {
            val model = Matrix4f().translate(pos.x, pos.y, pos.z).rotateY(rot).scale(w, h, 1f)
            world.renderer?.submit(quad, model, color)
        }

        fun drawCircle(pos: Vector3f, radius: Float, color: Vector3f) {
            val model = Matrix4f().translate(pos.x, pos.y, pos.z).scale(radius, radius, 1f)
            world.renderer?.submit(circle, model, color)
        }
        // TODO ===========================================================================================

        // For reference on the scene
        drawRect(Vector3f(0f, 0f, -1f), 5f, 10f, 0f, ColorVecs.WHITE)

        // Draw the "Player"
        drawCircle(this.pos, 1.5f, ColorVecs.GREEN)
    }
}