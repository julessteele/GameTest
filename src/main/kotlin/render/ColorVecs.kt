package render

import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.joml.Vector4fc

/**
 * Common RGB(A) colors as JOML vectors, components in 0f..1f.
 *
 * The constants are typed as the read-only Vector3fc / Vector4fc interfaces so
 * nobody can accidentally mutate a shared instance. Use rgb()/rgba() or
 * .copy() when you need a mutable vector.
 */
object ColorVecs {

    val WHITE: Vector3f = Vector3f(1f, 1f, 1f)
    val BLACK: Vector3f = Vector3f(0f, 0f, 0f)
    val RED: Vector3f = Vector3f(1f, 0f, 0f)
    val GREEN: Vector3f = Vector3f(0f, 1f, 0f)
    val BLUE: Vector3f = Vector3f(0f, 0f, 1f)
    val YELLOW: Vector3f = Vector3f(1f, 1f, 0f)
    val CYAN: Vector3f = Vector3f(0f, 1f, 1f)
    val MAGENTA: Vector3f = Vector3f(1f, 0f, 1f)
    val ORANGE: Vector3f = Vector3f(1f, 0.5f, 0f)
    val PURPLE: Vector3f = Vector3f(0.5f, 0f, 0.5f)
    val GRAY: Vector3f = Vector3f(0.5f, 0.5f, 0.5f)

    val TRANSPARENT: Vector4fc = Vector4f(0f, 0f, 0f, 0f)

    /** Mutable copy of a Vector3fc. */
    fun Vector3fc.copy(): Vector3f = Vector3f(this)

    /** Vec4 version of a color with the given alpha. */
    fun Vector3fc.withAlpha(a: Float = 1f): Vector4f = Vector4f(x(), y(), z(), a)

    /** From 0..255 channel values. */
    fun rgb(r: Int, g: Int, b: Int): Vector3f =
        Vector3f(r / 255f, g / 255f, b / 255f)

    fun rgba(r: Int, g: Int, b: Int, a: Int = 255): Vector4f =
        Vector4f(r / 255f, g / 255f, b / 255f, a / 255f)

    /** From a hex int like 0xFF8800 (RGB). */
    fun hex(rgb: Int): Vector3f =
        Vector3f(
            ((rgb shr 16) and 0xFF) / 255f,
            ((rgb shr 8) and 0xFF) / 255f,
            (rgb and 0xFF) / 255f
        )

    /** From a hex int like 0xFF8800CC (RGBA). */
    fun hexA(rgba: Int): Vector4f =
        Vector4f(
            ((rgba shr 24) and 0xFF) / 255f,
            ((rgba shr 16) and 0xFF) / 255f,
            ((rgba shr 8) and 0xFF) / 255f,
            (rgba and 0xFF) / 255f
        )
}
