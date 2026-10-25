package objects

import world.World

abstract class GameObject(val name: String, var moving: Boolean = false) {

    abstract fun update(dt: Float)
    abstract fun tick(dt: Float, world: World)
    abstract fun render(dt: Float, world: World)
}
