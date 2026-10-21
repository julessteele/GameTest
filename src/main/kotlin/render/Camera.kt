package render

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(
    var viewHeight: Float = 20f,   // world units visible vertically (zoom)
    var aspect: Float = 16f / 9f,
    var near: Float = 0.1f,
    var far: Float = 100f) {

    val position = Vector3f(0f, 0f, 10f)
    val target = Vector3f()
    private val up = Vector3f(0f, 1f, 0f)

    val view = Matrix4f()
    val projection = Matrix4f()
    val viewProjection = Matrix4f()
    private val invViewProj = Matrix4f()

    fun follow(x: Float, y: Float) {
        position.set(x, y, 10f)
        target.set(x, y, 0f)
    }

    fun update() {
        val halfH = viewHeight / 2f
        val halfW = halfH * aspect
        view.identity().lookAt(position, target, up)
        projection.identity().ortho(-halfW, halfW, -halfH, halfH, near, far)
        projection.mul(view, viewProjection)
    }

    /** Cursor (window coords, not framebuffer) -> world XY. Call after update(). */
    fun screenToWorld(mx: Double, my: Double, winW: Int, winH: Int, out: Vector3f) {
        val ndcX = (2.0 * mx / winW - 1.0).toFloat()
        val ndcY = (1.0 - 2.0 * my / winH).toFloat()
        viewProjection.invert(invViewProj)
        invViewProj.transformProject(ndcX, ndcY, 0f, out)
        out.z = 0f
    }
}
