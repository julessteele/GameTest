package objects.entity

import input.Input
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import render.Camera
import render.ColorVecs
import render.Mesh
import render.Texture
import world.World
import kotlin.math.sqrt

class PlayerEntity(
    name: String = "PlayerEntity",
    maxHp: Float = 20f,
    hp: Float = maxHp,
    pos: Vector3f = Vector3f(),
    width: Float = 1f,
    height: Float = 1f,
    var cam: Camera = Camera()) :
    Entity(name, maxHp, hp, pos, width, height) {

    private val moveSpeed = 100f

    private val sprite = Texture.fromFile("player.png")

    override fun update(dt: Float) {

        super.update(dt)

        // Prevent diagonal movement speedup
        val diagonal = (Input.isPressed(GLFW_KEY_W) != Input.isPressed(GLFW_KEY_S)) &&
                       (Input.isPressed(GLFW_KEY_A) != Input.isPressed(GLFW_KEY_D))
        val x = if (diagonal) 1f/sqrt(2.0).toFloat() else 1f

        // ==== Handle player movement ====
        if (Input.isPressed(GLFW_KEY_W))
            this.velocity.y += moveSpeed * dt * x
        if (Input.isPressed(GLFW_KEY_S))
            this.velocity.y -= moveSpeed * dt * x
        if (Input.isPressed(GLFW_KEY_A))
            this.velocity.x -= moveSpeed * dt * x
        if (Input.isPressed(GLFW_KEY_D))
            this.velocity.x += moveSpeed * dt * x
    }

    override fun tick(dt: Float, world: World) {
    }

    override fun render(dt: Float, world: World) {

        // TODO ========= Remove later - for testing ======================================================
        fun drawRect(pos: Vector3f, w: Float, h: Float, rot: Float, color: Vector3f) {
            val model = Matrix4f().translate(pos.x, pos.y, pos.z).rotateY(rot).scale(w, h, 1f)
            world.renderer?.submit(Mesh.quad(), model, color)
        }

        fun drawCircle(pos: Vector3f, radius: Float, color: Vector3f) {
            val model = Matrix4f().translate(pos.x, pos.y, pos.z).scale(radius, radius, 1f)
            world.renderer?.submit(Mesh.circle(), model, color)
        }
        // TODO ===========================================================================================

        // For reference on the scene
        drawRect(Vector3f(0f, 0f, -1f), 10f, 7f, 0f, ColorVecs.WHITE)

        // Draw the "Player"
        val pAspect = sprite.width.toFloat() / sprite.height
        val pModel = Matrix4f().translate(pos.x, pos.y, pos.z).scale(height * pAspect, height, 1f)
        world.renderer?.submitSprite(Mesh.quad(), pModel, sprite)
    }
}