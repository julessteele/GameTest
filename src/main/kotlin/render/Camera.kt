package render

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(
    val position: Vector3f = Vector3f(0f, 2f, 5f),
    val target: Vector3f = Vector3f(0f, 0f, 0f),
    var fovDegrees: Float = 70f,
    var aspect: Float = 16f / 9f,
    var near: Float = 0.1f,
    var far: Float = 500f,
) {
    private val up = Vector3f(0f, 1f, 0f)
    val view = Matrix4f()
    val projection = Matrix4f()
    val viewProjection = Matrix4f()

    fun update() {
        view.identity().lookAt(position, target, up)
        projection.identity().perspective(Math.toRadians(fovDegrees.toDouble()).toFloat(), aspect, near, far)
        projection.mul(view, viewProjection)
    }
}