package entity.comp

class EntityFlags {

    private var flags:MutableList<EntityFlagTypes> = mutableListOf()

    fun addFlag(flag:EntityFlagTypes) = flags.add(flag)
    fun removeFlag(flag:EntityFlagTypes) = flags.remove(flag)
    fun findFlag(flag:EntityFlagTypes):EntityFlagTypes? = flags.find { it == flag }
    fun isDead(): Boolean = flags.find { it == EntityFlagTypes.ENTITY_ALIVE } == null
}