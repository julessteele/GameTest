package engine

object GameClock {

    private var lastTime = System.nanoTime()

    /** Seconds elapsed between the last two ticks, scaled by timeScale. */
    var deltaTime: Float = 0f
        private set

    /** Seconds elapsed between the last two ticks, ignoring timeScale (use for UI, menus). */
    var unscaledDeltaTime: Float = 0f
        private set

    /** Total scaled seconds since start. */
    var totalTime: Double = 0.0
        private set

    var frameCount: Long = 0
        private set

    /** 1 = normal, 0 = paused, 0.5 = slow-mo. */
    var timeScale: Float = 1f

    /** Caps huge deltas (window drag, breakpoint, alt-tab) so physics doesn't explode. */
    var maxDelta: Float = 0.1f

    fun tick() {
        val now = System.nanoTime()
        val raw = ((now - lastTime) / 1_000_000_000.0).toFloat()
        lastTime = now

        unscaledDeltaTime = raw.coerceAtMost(maxDelta)
        deltaTime = unscaledDeltaTime * timeScale
        totalTime += deltaTime
        frameCount++
    }

    fun reset() {
        lastTime = System.nanoTime()
        deltaTime = 0f
        unscaledDeltaTime = 0f
    }
}