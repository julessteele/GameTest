package entity.comp

import entity.Entity

class EntityStats<T : Entity>(val parent: T, val maxHp: Float, var hp: Float = maxHp, var level: Int = 1) {

    fun hurt(n: Float) = this.hp - n

    fun heal(n: Float) = this.hp + n

    fun levelUp(n: Int = 1) = this.level + n
}