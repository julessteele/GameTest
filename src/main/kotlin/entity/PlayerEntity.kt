package entity

import input.Input
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import render.Camera

class PlayerEntity(
    name: String = "PlayerEntity",
    maxHp: Float = 20f,
    hp: Float = maxHp,
    pos: Vector3f = Vector3f(),
    width: Float = 1f,
    height: Float = 1f,
    var cam: Camera) :
    Entity(name, maxHp, hp, pos, width, height) {

    private val moveSpeed = 10f

    override fun tick(dt: Float) {

        // ==== Handle player movement ====
        if (Input.isPressed(GLFW_KEY_W))
            pos.x += moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_S))
            pos.x -= moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_A))
            pos.y -= moveSpeed * dt
        if (Input.isPressed(GLFW_KEY_D))
            pos.y -= moveSpeed * dt
    }

    override fun render() {

    }
}