package input

object Input {

    // The set of the all keys currently being pressed
    val keySet = mutableSetOf<Int>()

    fun isPressed(key: Int): Boolean = keySet.contains(key)
}